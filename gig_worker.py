from flask import Flask, request, jsonify
import pandas as pd
import json
from ibm_watsonx_ai import Credentials
from ibm_watsonx_ai.foundation_models import TSModelInference
from ibm_watsonx_ai.foundation_models.schema import TSForecastParameters
import numpy as np
from datetime import datetime, timedelta
from pyngrok import ngrok
import random
import requests
import json
from botocore.client import Config
import ibm_boto3



def data_generate():
    # Get current date
    current_date = datetime.now()

    # Calculate start date (3 years ago)
    start_date = current_date - timedelta(days=3*365)

    # Define parameters
    num_workers = 100
    platforms = ["Uber", "Zomato", "Swiggy", "Rapido"]
    cities = ["Mumbai", "Delhi", "Bangalore", "Hyderabad"]

    # Generate synthetic data
    records = []
    worker_id_counter = 1

    for platform in platforms:
        for city in cities:
            for week in range(int((current_date - start_date).days / 7)):
                week_start = current_date - timedelta(weeks=week)
                for _ in range(num_workers):
                    worker_id = f"W{str(worker_id_counter).zfill(4)}"
                    worker_id_counter += 1

                    hours_worked = np.random.randint(25, 60)
                    base_rate = random.uniform(100, 160)
                    holiday_flag = 1 if week_start.weekday() in [5, 6] else 0
                    gross_pay = round(hours_worked * base_rate * (1 - 0.1 * random.random()), 2)
                    tips = round(gross_pay * random.uniform(0.05, 0.15), 2)
                    platform_fee = round(gross_pay * random.uniform(0.1, 0.25), 2)

                    base_price = {
                        "Mumbai": 105,
                        "Delhi": 97,
                        "Bangalore": 102,
                        "Hyderabad": 108
                    }[city]
                    petrol_price = round(base_price + random.uniform(-2.0, 2.0), 2)
                    petrol_idx = round(petrol_price / base_price, 3)
                    cpi = round(random.uniform(120, 150), 2)

                    temperature = round(random.uniform(22, 38), 1)
                    humidity = round(random.uniform(40, 90), 1)
                    rainfall = round(random.uniform(0, 150), 1)
                    weather_index = round(random.uniform(0.8, 1.2), 2)

                    records.append({
                        "worker_id": worker_id,
                        "platform": platform,
                        "city": city,
                        "week_start": week_start.strftime("%Y-%m-%d"),
                        "hours_worked": hours_worked,
                        "gross_pay": gross_pay,
                        "tips": tips,
                        "platform_fee": platform_fee,
                        "petrol_price": petrol_price,
                        "petrol_price_idx": petrol_idx,
                        "cpi": cpi,
                        "holiday_flag": holiday_flag,
                        "weather_idx_input": weather_index,
                        "temperature": temperature,
                        "humidity": humidity,
                        "rainfall": rainfall
                    })

    # Convert records to DataFrame
    df = pd.DataFrame(records)

    # Save to JSON file
    json_file_path = "combined_gig_worker_data.json"
    df.to_json(json_file_path, orient="records", lines=False)

    # Upload to IBM Cloud Object Storage
    # cos_client = ibm_boto3.client(service_name='s3',
    #     ibm_api_key_id='LH5kp4cMyNvSjd0B8nuCQtcvLSManSoijLA9RytKXSdH',
    #     ibm_auth_endpoint="https://iam.cloud.ibm.com/identity/token",
    #     config=Config(signature_version='oauth'),
    #     endpoint_url='https://s3.direct.us-south.cloud-object-storage.appdomain.cloud')

    with open(json_file_path, "rb") as f:
        cos_client.upload_fileobj(
            Fileobj=f,
            Bucket='gig-workers-fair-pay-monitor',
            Key='combined_gig_worker_data.json'
        )




app = Flask(__name__)

# Define IBM Watson X AI credentials
IAM_API_KEY = "h3UwMhi8G5pgenC6s_QWVUXEGXmWTlh4bPfycOT6NjqY"
project_id = "f60f72fa-e445-4de8-922b-b22bf2cff40f"

# Set up credentials
creds = Credentials(
    api_key=IAM_API_KEY,
    url="https://us-south.ml.cloud.ibm.com"
)

# Initialize model inference

tst = TSModelInference(
    model_id="ibm/granite-ttm-512-96-r2",
    credentials=creds,
    project_id=project_id
)

def get_params(input_length):
# Define forecast parameters
    params = TSForecastParameters(
        timestamp_column="week_start",
        prediction_length=input_length,
        id_columns=["city","platform"],  # Important: Tell the model to split time series by this
        freq="1W",
        target_columns=["gross_pay"]
    )
    return params

# Load data from IBM COS

# cos_client = ibm_boto3.client(service_name='s3',
#                                 ibm_api_key_id='LH5kp4cMyNvSjd0B8nuCQtcvLSManSoijLA9RytKXSdH',
#                                 ibm_auth_endpoint="https://iam.cloud.ibm.com/identity/token",
#                                 config=Config(signature_version='oauth',connect_timeout=600, read_timeout=600),
#                                 endpoint_url='https://s3.direct.us-south.cloud-object-storage.appdomain.cloud')
# body = cos_client.get_object(Bucket='gig-workers-fair-pay-monitor', Key='combined_gig_worker_data.json')['Body']
# df_combined = pd.read_json(body, orient='records')
df_combined = pd.read_json('combined_gig_worker_data.json', orient='records')
df_combined["week_start"] = pd.to_datetime(df_combined["week_start"])
df_combined = df_combined[df_combined["gross_pay"].notna()].sort_values(by="week_start")


def data_dict_compute(df):
    data_dict = df.copy()
    data_dict["week_start"] = data_dict["week_start"].dt.strftime("%Y-%m-%d")
    data_dict = data_dict.to_dict(orient="list")
    for key in data_dict:
        data_dict[key] = [x if pd.notna(x) else None for x in data_dict[key]]
    return data_dict

@app.route('/user_data_comparision_for_provided_week', methods=['POST'])
def user_data_comparision_for_provided_week():
    user_df = pd.DataFrame(request.get_json())
    user_df["week_start"] = pd.to_datetime(user_df["week_start"])
    user_df = user_df[user_df["gross_pay"].notna()].sort_values(by="week_start")
    oldest_date = user_df["week_start"].min()
    filtered_df = df_combined[df_combined["week_start"] < oldest_date]
    data_dict_past  = data_dict_compute(filtered_df)
    params = get_params(len(user_df))
    forecast = tst.forecast(data=data_dict_past, params=params)
    forecast_df = pd.DataFrame(forecast['results'][0])
    forecast_df = forecast_df[
    (forecast_df["city"] == user_df['city'].unique()[0]) &
    (forecast_df["platform"] == user_df['platform'].unique()[0])]    
    return jsonify(forecast_df.to_dict(orient='records'))

@app.route('/user_data_next_week_forcast', methods=['POST'])
def user_data_next_week_forcast():
    user_df = pd.DataFrame(request.get_json())
    user_df["week_start"] = pd.to_datetime(user_df["week_start"])
    user_df = user_df[user_df["gross_pay"].notna()].sort_values(by="week_start")

    # Split user data
    actual_df = user_df.tail(7).copy()
    training_df = user_df.iloc[:-7].copy()

    df_combined["week_start"] = pd.to_datetime(df_combined["week_start"])
    actual_df["week_start"] = pd.to_datetime(actual_df["week_start"])
    training_df["week_start"] = pd.to_datetime(training_df["week_start"])

    # Merge current worker input with combined data
    df_all = pd.concat([df_combined, training_df], ignore_index=True)
    data_dict_future  = data_dict_compute(df_all)

    # Perform forecasting
    params = get_params(len(user_df))
    forecast = tst.forecast(data=data_dict_future, params=params)
    forecast_df = pd.DataFrame(forecast['results'][0])

    return jsonify(forecast_df.to_dict(orient='records'))

if __name__ == "__main__":
    # data_generate()
    ngrok.set_auth_token("2wcasOHhuwchRDpqv4lHNoECAes_4Gj93pGvbvrWojekAbmBw")
    http_tunnel = ngrok.connect(8000)
    print(f'ngrok tunnel: {http_tunnel.public_url}')
    app.run(host='0.0.0.0',port=8000,debug=True)

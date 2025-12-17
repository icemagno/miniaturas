# -*- coding: utf-8 -*-

import requests
from bs4 import BeautifulSoup
import json
import os

collection_name = "2025"
url = "https://hotwheels.fandom.com/wiki/List_of_"+collection_name+"_Hot_Wheels"
target_folder = "./" + collection_name
output_file = target_folder+"/data.json"


def extract_hotwheels_list(url, output_file):
    # Request the content of the webpage
    response = requests.get(url)
    if response.status_code != 200:
        print(f"Failed to fetch the webpage: {response.status_code}")
        return

    soup = BeautifulSoup(response.content, 'html.parser')

    # Locate the relevant table or list containing Hot Wheels data
    table = soup.find('table', {'class': 'wikitable'})
    if not table:
        print("No table found on the webpage.")
        return

    os.makedirs(target_folder, exist_ok=True)

    # Extract data from the table
    hot_wheels_list = []
    rows = table.find_all('tr')
    for row in rows[1:]:  # Skip the header row
        cols = row.find_all('td')
        if len(cols) < 3:
            continue

        code = cols[0].text.strip()
        name = cols[2].text.strip()
        image_tag = cols[5].find('a')
        image_url = image_tag['href'] if image_tag else None

        if image_url:
            # Download the image and save it locally
            try:
                img_response = requests.get(image_url, stream=True)
                if img_response.status_code == 200:
                    image_path = os.path.join(target_folder, f"{code}.jpg")
                    with open(image_path, 'wb') as img_file:
                        for chunk in img_response.iter_content(1024):
                            img_file.write(chunk)
            except Exception as e:
                print(f"Failed to download image for {code}: {e}")

        hot_wheels_list.append({
            "Codigo": code,
            "Nome": name,
            "Imagem": image_url
        })

    # Save the data to a JSON file
    with open(output_file, 'w') as json_file:
        json.dump(hot_wheels_list, json_file, indent=4)

    print(f"Data extracted and saved to {output_file}")

# URL of the webpage containing the Hot Wheels list
extract_hotwheels_list(url, output_file)

import os
import urllib.request
import zipfile

MODEL_URL = "http://161.35.113.35:1234/bert-model.zip"
MODEL_ZIP_FILENAME = "bert-model.zip"
MODEL_DIRECTORY = "bert-model"

def main():
    print("Start fetching model")
    if not os.path.exists(MODEL_ZIP_FILENAME):
        response = urllib.request.urlopen(MODEL_URL)
        with open(MODEL_ZIP_FILENAME, "wb") as model_file:
            model_file.write(response.read())
        print("bert-model.zip has been downloaded")
    else:
        print("bert-model.zip is already exists")
    if not os.path.exists(MODEL_DIRECTORY):
        with zipfile.ZipFile(MODEL_ZIP_FILENAME, 'r') as zip_ref:
            zip_ref.extractall(".")
        print("Model has been extracted")
    else:
        print("model directory is already exists")


if __name__ == '__main__':
    main()

import os
import urllib.request
import zipfile

CORPUS_URL = "http://161.35.113.35:1234/corpus.zip"
CORPUS_ZIP_FILENAME = "corpus.zip"
CORPUS_DIRECTORY = "corpus"

def main():
    print("Start fetching corpus")
    if not os.path.exists(CORPUS_ZIP_FILENAME):
        response = urllib.request.urlopen(CORPUS_URL)
        with open(CORPUS_ZIP_FILENAME, "wb") as corpus_file:
            corpus_file.write(response.read())
        print("corpus.zip has been downloaded")
    else:
        print("corpus.zip is already exists")
    if not os.path.exists(CORPUS_DIRECTORY):
        with zipfile.ZipFile(CORPUS_ZIP_FILENAME, 'r') as zip_ref:
            zip_ref.extractall(".")
        print("Corpus has been extracted")
    else:
        print("corpus directory is already exists")


if __name__ == '__main__':
    main()

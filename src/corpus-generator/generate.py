import os
import re
import time

import wikipedia


START_PAGE_TITLE = "Music"
PAGES_COUNT = 4000
CORPUS_DIRECTORY = "corpus"
HEADER_PATTERN = re.compile("==.+==")
WORDS_PATTERN = re.compile(r"\W+")



def remove_non_words(title: str):
    return WORDS_PATTERN.sub('', title)


def remove_headers(content: str):
    return HEADER_PATTERN.sub('', content)


def save_page(page):
    file_path = os.path.join(CORPUS_DIRECTORY, remove_non_words(page.title))
    with open(file_path, "w") as page_file:
        page_file.write(remove_headers(page.content))


def main():
    visited_pages = set()
    pages_queue = [START_PAGE_TITLE]

    current_pages_index = 0
    saved_pages_count = 0

    while saved_pages_count < PAGES_COUNT:
        page_title_candidate = pages_queue[current_pages_index]
        if page_title_candidate not in visited_pages:
            visited_pages.add(page_title_candidate)

            try:
                new_page = wikipedia.page(page_title_candidate)
                save_page(new_page)
                pages_queue.extend(new_page.links)
                saved_pages_count += 1
                print(f"Page '{page_title_candidate}' has been saved successfully. Total saved pages count: {saved_pages_count}")
            except wikipedia.exceptions.WikipediaException as e:
                print("Warning:", e)
        else:
            print(f"Page {page_title_candidate} is already visited. Skipping")
        current_pages_index += 1


if __name__ == '__main__':
    main()

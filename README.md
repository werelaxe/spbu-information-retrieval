# spbu-information-retrieval

## Requirements
* Python 3.6+ for a corpus generating;
* JRE 1.8+ for building index and performing the queries.

## Usage
1) Generate or fetch the corpus;
2) Make a fat jar and run it for building the inverted index and performing the queries.

### Corpus generating
1) Install the package `wikipedia` for python3 via `pip3 install wikipedia`; 
2) Move to the directory `src/corpus-generator`;
3) Run generator: `python3 generate.py`.

### Corpus fetching (recommended)
1) Move to the directory `src/corpus-generator`;
2) Run fetcher: `python3 fetch.py`.

### Inverted index
1) Run gradle task `fatJar`: `./gradlew fatJar`;
2) Run the application: `java -jar ./build/libs/information-retrieval-1.0-SNAPSHOT.jar` from the project root directory;
3) Wait for the inverted index creating.

### Query syntax
```
<expr> ::= <term> "|" <expr> | <term>
<term> ::= <factor> "&" <term> | <factor>
<factor> ::= "!"? ((" <expr> ")" |  <word>)
<word> ::= string
```

### Query example
```
Query: hello & !(apple | night)
Related documents: [MusicofNewZealand, Preschool, EnergyCrisis74, Latin, Blottoband, Canadianname, Targetgirl, Phonographcylinder, PaulWilliamssaxophonist, RCARecords, SomethingBeatlessong, Musicdownload, Ritual, PennyLane, TMobile]
```

# spbu-information-retrieval

## Requirements
* Python 3.6+ for a corpus generating;
* JRE 1.8+ for building index and performing the queries.


## Features
* Embedding-based document ranking and deduplications search with [pre-trained BERT model](https://github.com/google-research/bert#bert) (L=2, H=128).

## Usage
1) Generate or fetch the corpus;
2) Fetch default BERT model (or create manually);
3) Make a fat jar and run it for building the inverted index and performing the queries.

### Corpus generating
1) Install the package `wikipedia` for python3 via `pip3 install wikipedia`; 
2) Move to the directory `src/corpus-generator`;
3) Run generator: `python3 generate.py`.

### Corpus fetching (recommended)
1) Move to the directory `src/corpus-generator`;
2) Run fetcher: `python3 fetch.py`.

### Default model fetching (recommended)
1) Move to the directory `src/bert-model-fetcher`;
2) Run fetcher: `python3 fetch.py`.

### Manual model creating
1) Put your model to the directory `src/bert-model-fetcher/bert-model`.

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

### Query example with syntax usage
```
Query: 'hello & !(apple | night)'
Related documents count: 15

Document                 Difference
Canadianname:            0.23571063610143028
Blottoband:              0.2458328421053011
Targetgirl:              0.27015218912856653
Phonographcylinder:      0.29886991607781965
MusicofNewZealand:       0.31419606917188503
EnergyCrisis74:          0.33686972316354513
PaulWilliamssaxophonist: 0.340099402324995
Ritual:                  0.3546883294475265
TMobile:                 0.36016620250302367
Musicdownload:           0.3672260037565138
```

### Query example with duplicates
```
Query: 'Mathematics (from Greek μάθημα máthēma, "knowledge, study, learning") includes the study of such topics as quantity (number theory)'
Related documents count: 744

Document                                        Difference
MathematicsDup1:                                0.12470580099034123, duplicates: MathematicsDup2, MathematicsDup4, MathematicsDup3, Mathematics, MathematicsDup0
Philosophy:                                     0.13979125776677392
Doctorate:                                      0.1560577917844057
Mathematicalbeauty:                             0.15844223645399325
Empiricalevidence:                              0.16079635175810836
DegreesoftheUniversityofOxford:                 0.16082743086735718
Quadrivium:                                     0.16434404691972304
Listofimportantpublicationsinphilosophy:        0.16476143152976874
Processphilosophy:                              0.16598192919627763
Sophist:                                        0.1668163339199964
```

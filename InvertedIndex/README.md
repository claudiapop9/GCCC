# Hadoop Project: 
### Requirement ###
Write an inversed index app for a set of books as data. (you can download text books from project gutenberg – using text format). 
In order to build the inversed index you need to account for a stop word list (words that will not be indexed by the application. Ex: and, or, how, so, etc). These stop words will be read by the application from a text file (stopwords.txt)
An inversed index contains for each distinct unique word, a list of files containing the given word with its location within the file (line number). 
When running the application you should have a small cluster/cloud of at least two nodes build from VMs – eventually a larger cluster build from all your individual VMs.

Ex: word: (file#1, line#1, line#2, ….) (file#4, line#1, line#2,…) …)

### Proposed solution ###

In order to skip the words, the stopwords.txt is added to cache and a boolean is set to true in order to know that we have words to skip;
####InvertedIndexMapper:####
1. Checks if there are words that had to be skipped and set the file to cache;
2. When creating the map it splits the file when finding a new line ("\n") in order to obtain the row number;
So if we find a new line we increase the rowcounter;
3. The current line is split in order to obtain the words and if the stopWords file does not contain the current word then the key: (word:file) and the value: line number is set;
Format: word: file, line

####InvertedIndexCombinare:####
Using reduce method we obtain the mapping in the folowing format: (word, (file, line, line...))

####InvertedIndexReducer:####
We obtain the final result which is has the following format: word: (file#1, line#1, line#2, ….) (file#4, line#1, line#2,…)


### How to run the project ###
After the virtual machines are configured, the command has the following format: hadoop jar InvertedIndex.jar /input /output -skip /stopwords.txt
Input: folder with text files, I used books taken from project gutenberg;
Output: folder where the result will be inserted. 
*Note the folder should not exist when running the project (to delete the folder and its contents run: hdfs dfs -rm -r /output )
Stopwords.txt: file with words that will not be indexed by the application. Ex: and, or, how, so, etc








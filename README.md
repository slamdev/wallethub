# wallethub [![Build Status](https://travis-ci.org/slamdev/wallethub.svg?branch=master)](https://travis-ci.org/slamdev/wallethub)
## SQL
1. Write a query to rank order the following table in MySQL by votes, display the rank as one of the columns.
```
CREATE TABLE votes ( name CHAR(10), votes INT );
INSERT INTO votes VALUES ('Smith',10), ('Jones',15), ('White',20), ('Black',40), ('Green',50), ('Brown',20);
```
2. Write a function to capitalize the first letter of a word in a given string;
Example: `initcap(UNITED states Of AmERIca ) = United States Of America`
3. Write a procedure in MySQL to split a column into rows using a delimiter.
```
CREATE TABLE sometbl ( ID INT, NAME VARCHAR(50) );
INSERT INTO sometbl VALUES (1, 'Smith'), (2, 'Julio|Jones|Falcons'), (3,'White|Snow'), (4, 'Paint|It|Red'), (5, 'Green|Lantern'), (6, 'Brown|bag');
```
For (2), example rows would look like >> `"3, white", "3, Snow" ...`
4. I have a table for bugs from a bug tracking software; let’s call the table “bugs”.
The table has four columns `(id, open_date, close_date, severity)`.
On any given day a bug is open if the `open_date` is on or before that day and `close_date` is after that day. 
For example, a bug is open on `2012-01-01`, if it’s created on or before `2012-01-01` and closed on or after `2012-01-02`.
I want a SQL to show number of bugs open for a range of dates
## JAVA
1. Write an efficient algorithm to check if a string is a palindrome. A string is a palindrome if the string matches the reverse of string.
Example: `1221` is a palindrome but not `1121`.
2. Write an efficient algorithm to find K-complementary pairs in a given array of integers.
Given `Array A`, pair `(i, j)` is K-complementary if `K = A[i] + A[j]`;
3. Given a large file that does not fit in memory (say 10GB), find the top 100000 most frequent phrases. 
The file has 50 phrases per line separated by a pipe (|).
Assume that the phrases do not contain pipe.
Example line may look like: 
```
Foobar Candy | Olympics 2012 | PGA | CNET | Microsoft Bing ...
```
The above line has 5 phrases in visible region.
## Instructions
1. Your code will be graded both on correctness and efficiency. There are multiple correct answers for each question. You should provide only one answer for each question (the one that is the most efficient).
2. Where appropriate, write comments in your code that explain your assumptions and design decisions. Extra credit will be given if in your comments you provide an estimate of the time and space efficiency of your code in "Big O" notation.
3. Provide your answers in a single archive file (.zip or .tar.gz), using your name for the filename. For example: JasmineEvans.tar.gz
4. The archive file should contain only two directories (answers,tests) with only the following files. All files are in plain text format.
```
/answers/README.txt  (optional)
/answers/q1.sql
/answers/q2.sql
/answers/q3.sql
/answers/q4.sql
/answers/Palindrome.java
/answers/ComplementaryPairs.java
/answers/TopPhrases.java
```
```
/tests/q1.txt
/tests/q2.txt
/tests/q3.txt
/tests/q4.txt
/tests/Palindrome.txt
/tests/ComplementaryPairs.txt
/tests/TopPhrases.txt
```
5. For the Java questions, if you need to implement any helper classes, include them as inner classes in the main class. Do not include any extra files in the archive file, other than the ones listed above. If you need to use a third-party library, you can do so, but do not include it in the archive file. Just add a comment in your code that indicates from where you obtained the library.
6. Even if you can't answer a question using code, we would like to know your approach on how you would solve the problem. Write any of your thoughts and ideas in the optional README.txt file. You can also use the README.txt file to elaborate on any of your other answers.
7. You will be graded on how well you tested your code and on your selection of test cases. Document your test cases in the text files in the /tests/ directory. Where appropriate, include the input to your code and the output of your code in these text files. For test cases where the input or output is very large in size do not include it in the text file.

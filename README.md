# garbageCollectionAnalyse
Analyse java garbage collection logs 

###abstract

* Analyse java garbage collector log.
* Measure the pause time in miliPerSecnd
* skipLines <count>- skips first <count> lines, so only last lines are analysed
*  expected format:   3.797: [GC (Allocation Failure)  854385K->28809K(1204736K), 0.0014854 secs]


###usage

Add logging to java Garbage collection

* java -verbose:gc -Xloggc:/var/js/java.log ...
* java -verbose:gc -Xloggc:/var/js/java.log  -XX:+ PrintGCDetails -XX:+PrintTenuringDistribution -XX:+PrintGCTimestamps ...
* java -verbose:gc -Xloggc:/var/js/java.log  -XX:+ PrintGCDetails -XX:+PrintGCTimestamps ...
* java -verbose:gc -Xloggc:/var/js/java.log  -XX:+PrintTenuringDistribution ...


Running grabageCollection analyser

java -verbose:gc -Xloggc:/var/js/java.log -jar  garbageCollectionAnalyse.jar  file=/var/js/java.log4  skipLines=1000

optional args:   
skipLines=integer                     number of lines to skip before analyze
file=string                           java garbage logfile /var/js/java.log0

actual args:  [file=/var/js/java.log4, ]

miliPerSec=8
lineCount=159217
countNoMatch=3


###arguments

Unique prefix of argument name is enough (No need to type full name).

* file=/var/js/java.log0
* skipLines=10000                         Skip first 10000 lines


### files

* garbageCollectionAnalyse.java
* Args.java                        Argumet parser for Java (MIT license)

### License

* MIT License, free to change and redistribute, Just keep the credit.
* Any question or requests are welcome

<h3>
<a id="authors-and-contributors" class="anchor" href="#authors-and-contributors" aria-hidden="true"><span aria-hidden="true" class="octicon octicon-link"></span></a>Authors and Contributors</h3>

<p><a href="mailto:eli.shagam@gmail.com">eli.shagam@gmail.com</a></p>

TechoPhil.com
~                                                                                      
~       


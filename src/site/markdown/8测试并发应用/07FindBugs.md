用 FindBugs 分析并发代码
====

静态代码分析工具是一个分析应用的源代码来查找潜在错误的工具集。
这些工具，如 Checkstyle, PMD 或 FindBugs 有一套按良好的习惯做法预定义的规则，并分析源代码查找对那些规则的违背之处。
目标是在被放到生产环境执行前，尽早找出错误或者导致性能低下的地方。
FindBugs 是这些分析 Java 代码的工具之一。它是包含一系列分析Java并发代码的规则的开源工具。

在本节中，将学习如何使用 FindBugs 来分析 Java 并发应用。


### 准备

[下载 FindBugs](http://findbugs.sourceforge.net/) 。可下载独立应用或Eclipse插件。在本节中，将使用独立应用版本。



### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt8.sect07 package中*




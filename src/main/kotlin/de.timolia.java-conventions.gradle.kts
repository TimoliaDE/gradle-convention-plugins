plugins {
    java
    checkstyle
}
checkstyle {
    config = resources.text.fromString(de.timolia.CheckstyleUtil.getCheckstyleConfig("/checkstyle.xml"))
    maxWarnings = 0
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
    options.encoding = "UTF-8"
}

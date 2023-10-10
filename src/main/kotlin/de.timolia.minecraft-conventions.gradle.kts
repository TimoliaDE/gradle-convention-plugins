plugins {
    java
}

fun DependencyHandlerScope.systems(vararg names: String) {
    for (name in names) {
        implementation(group = "timolia.systems", name = name, version = "git-master") {
            isChanging = true
        }
    }
}
fun DependencyHandlerScope.common(vararg names: String) {
    for (name in names) {
        implementation(group = "timolia.tcommon", name = name, version = "git-master") {
            isChanging = true
        }
    }
}
fun DependencyHandlerScope.core(vararg names: String) {
    for (name in names) {
        implementation(group = "timolia.core", name = name, version = "git-master") {
            isChanging = true
        }
    }
}
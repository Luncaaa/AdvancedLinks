val shadowBundle = configurations.create("shadowBundle") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val common = configurations.create("common") {
    isCanBeResolved = true
    isCanBeConsumed = false
}
rootProject.name = "tortoise"

fun defineSubProject(name: String, path: String) {
    include(name)
    project(":$name").projectDir = file(path)
}

defineSubProject("tortoise-app", "app")
allprojects {
    repositories {
        google()
        // mavenCentral()
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/google") }
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/jcenter") }
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/maven-central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

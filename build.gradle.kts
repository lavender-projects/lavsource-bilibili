@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.dependency.management) apply false
}

version = libs.versions.root.get()

allprojects {
    group = "de.honoka.lavender"

    apply(plugin = "io.spring.dependency-management")
}
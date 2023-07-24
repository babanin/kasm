import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import kotlinx.kover.gradle.plugin.dsl.MetricType

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.2"
    `java-library`
}

group = "net.babanin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    
    maven {
        url = uri("https://repository.ow2.org/nexus/content/repositories/releases/")
    }
}

dependencies {    
    testImplementation(kotlin("test"))  
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

koverReport {
    // configure default reports - for Kotlin/JVM or Kotlin/MPP projects or merged android variants  
    defaults {
        // filters for all default reports 
        filters {
            excludes {
                // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
                classes("com.example.*")
            }
        }

        // configure XML report
        xml {
            //  generate an XML report when running the `check` task
            onCheck = false

            // XML report file
            setReportFile(layout.buildDirectory.file("reports/kover/report.xml"))

            // overriding filters only for the XML report 
            filters {
                // exclusions for XML reports
                excludes {
                    // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                    classes("com.example.*")
                    // excludes all classes located in specified package and it subpackages, wildcards '*' and '?' are available
//                    packages("com.another.subpackage")
                    // excludes all classes and functions, annotated by specified annotations (with BINARY or RUNTIME AnnotationRetention), wildcards '*' and '?' are available
//                    annotatedBy("*Generated*")
                }

                // inclusions for XML reports
                includes {
                    // includes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                    classes("com.example.*")
                    // includes all classes located in specified package and it subpackages
                    packages("katartal")
                }
            }
        }

        // configure HTML report
        html {
            // custom header in HTML reports, project path by default
            title = "My report title"

            // custom charset in HTML report files, used return value of `Charset.defaultCharset()` for Kover or UTF-8 for JaCoCo by default.
            charset = "UTF-8"

            //  generate a HTML report when running the `check` task
            onCheck = false

            // directory for HTML report
            setReportDir(layout.buildDirectory.dir("reports/kover/html-result"))

            // overriding filters only for the HTML report
            filters {
                // exclusions for HTML reports
                excludes {
                    // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                    classes("com.example.*")
                    // excludes all classes located in specified package and it subpackages, wildcards '*' and '?' are available
//                    packages("com.another.subpackage")
                    // excludes all classes and functions, annotated by specified annotations (with BINARY or RUNTIME AnnotationRetention), wildcards '*' and '?' are available
//                    annotatedBy("*Generated*")
                }

                // inclusions for HTML reports
                includes {
                    // includes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                    classes("com.example.*")
                    // includes all classes located in specified package and it subpackages
                    packages("katartal")
                }
            }
        }

        // configure verification
        verify {
            //  verify coverage when running the `check` task
            onCheck = true

            // add verification rule
            rule {
                // check this rule during verification 
                isEnabled = true

                // specify the code unit for which coverage will be aggregated 
                entity = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION

                // overriding filters only for current rule
                filters {
                    excludes {
                        // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                        classes("com.example.*")
                        // excludes all classes located in specified package and it subpackages, wildcards '*' and '?' are available
//                        packages("com.another.subpackage")
                        // excludes all classes and functions, annotated by specified annotations (with BINARY or RUNTIME AnnotationRetention), wildcards '*' and '?' are available
//                        annotatedBy("*Generated*")
                    }
                    includes {
                        // includes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                        classes("com.example.*")
                        // includes all classes located in specified package and it subpackages
                        packages("katartal")
                    }
                }

                // specify verification bound for this rule
                bound {
                    // lower bound
                    minValue = 1

                    // upper bound
                    maxValue = 99

                    // specify which units to measure coverage for
                    metric = kotlinx.kover.gradle.plugin.dsl.MetricType.LINE

                    // specify an aggregating function to obtain a single value that will be checked against the lower and upper boundaries
                    aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                }

                // add lower bound for percentage of covered lines
                minBound(2)

                // add upper bound for percentage of covered lines
                maxBound(98)
            }
        }

        // configure coverage logging
        log {
            //  print coverage when running the `check` task
            onCheck = true

            // overriding filters only for the logging report
            filters {
                // exclusions for logging reports
                excludes {
                    // excludes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                    classes("com.example.*")
                    // excludes all classes located in specified package and it subpackages, wildcards '*' and '?' are available
//                    packages("com.another.subpackage")
                    // excludes all classes and functions, annotated by specified annotations (with BINARY or RUNTIME AnnotationRetention), wildcards '*' and '?' are available
//                    annotatedBy("*Generated*")
                }

                // inclusions for logging reports
                includes {
                    // includes class by fully-qualified JVM class name, wildcards '*' and '?' are available
//                    classes("com.example.*")
                    // includes all classes located in specified package and it subpackages
                    packages("katartal")
                }
            }
            // Add a header line to the output before the lines with coverage
            header = null
            // Format of the strings to print coverage for the specified in `groupBy` group
            format = "<entity> line coverage: <value>%"
            // Specifies by which entity the code for separate coverage evaluation will be grouped
            groupBy = GroupingEntityType.APPLICATION
            // Specifies which metric is used for coverage evaluation
            coverageUnits = MetricType.LINE
            // Specifies aggregation function that will be calculated over all the elements of the same group
            aggregationForGroup = AggregationType.COVERED_PERCENTAGE
        }
    }
}
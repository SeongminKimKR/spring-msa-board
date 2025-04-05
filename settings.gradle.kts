plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "spring-msa-board"

include("common")
include("common:snowflake")
include("service")
include("service:article")
include("service:comment")
include("service:view")
include("service:like")
include("service:hot-article")
include("service:article-read")

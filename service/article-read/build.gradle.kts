dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("io.mockk:mockk:1.13.4")
    implementation(project(":common:event"))
    implementation(project(":common:data-serializer"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
}






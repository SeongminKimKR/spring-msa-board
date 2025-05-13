package my.board.view

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["my.board"])
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["my.board"])
class ViewApplication

fun main(args: Array<String>) {
    runApplication<ViewApplication>(*args)
}

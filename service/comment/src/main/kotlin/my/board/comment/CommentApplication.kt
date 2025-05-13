package my.board.comment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["my.board"])
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["my.board"])
class CommentApplication

fun main(args: Array<String>) {
    runApplication<CommentApplication>(*args)
}

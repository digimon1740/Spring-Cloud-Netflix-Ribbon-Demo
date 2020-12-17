package com.example.digimon.spring.cloud.ribbon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RibbonApplication

fun main(args: Array<String>) {
	runApplication<RibbonApplication>(*args)
}

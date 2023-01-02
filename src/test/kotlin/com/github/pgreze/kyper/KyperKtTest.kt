package com.github.pgreze.kyper

import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas

class KyperKtTest {

    @Test
    fun `kyper on instance`() {
        val help = "help1"
        val kyper = PublicInternalPrivate().kyper(help = help)

        kyper.help shouldBe help
        kyper.commands.keys shouldHaveSingleElement "public"
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    @Test
    fun `kyper DSL`() {
        val help = "help2"
        val kyper = kyper(help = help) {
            register("f1") { -> }
            register("f2") { -> }
        }

        kyper.help shouldBe help
        kyper.commands.keys shouldBe setOf("f1", "f2")
    }
}

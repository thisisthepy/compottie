package js

import io.github.alexzhirkevich.skriptie.common.ReferenceError
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FunctionsTest {

    @Test
    fun creation() {
        """
            var x;
            function test(a,b) { return a + b }
            x = test(1,2)
        """.trimIndent().eval().assertEqualsTo(3L)

        """
            function test(a,b) {
                return a + b
            }
            test(1,2)
        """.trimIndent().eval().assertEqualsTo(3L)

        """
            function test(a,b)
            {
                let x = b + 1
                return a + x
            }
            test(1,2)
        """.trimIndent().eval().assertEqualsTo(4L)
    }

    @Test
    fun defaultArgs(){
        """
            function test(a, b = 2)
            {
                return  a + b
            }
            test(1)
        """.trimIndent().eval().assertEqualsTo(3L)

        """
            var x;
            function test(a, b = 2)
            {
                return a + b
            }
            x = test(2,3)
        """.trimIndent().eval().assertEqualsTo(5L)

        """
            function test(a = 1, b = 2)
            {
                return a + b
            }
            test()
        """.trimIndent().eval().assertEqualsTo(3L)
    }

    @Test
    fun scope(){
        """
            let x = 1
            
            function fun1(){
                function fun2() {
                    x = 2
                }
                
                fun2()
            }
            fun1()
            
            x
        """.trimIndent().eval().assertEqualsTo(2L)

        assertFailsWith<ReferenceError> {
            """
            function fun1(){
                function fun2() {
                    
                }
            }
            fun2()
        """.trimIndent().eval().assertEqualsTo(2L)
        }
    }
}
package org.dhatim.safesql;

import org.dhatim.safesql.assertion.Assertions;
import org.junit.Test;

public class SafeSqlizableChainTest {

    private static final class Constant implements SafeSqlizable {

        @Override
        public void appendTo(SafeSqlAppendable builder) {
            builder.append("SELECT");
        }

    }

    @Test
    public void testAppendTo() {
        Constant c = new Constant();
        SafeSqlizableChain chain = new SafeSqlizableChain(c, c, c, c);
        Assertions.assertThat(chain.toSafeSql())
            .hasSql("SELECTSELECTSELECTSELECT")
            .hasEmptyParameters();
    }

}

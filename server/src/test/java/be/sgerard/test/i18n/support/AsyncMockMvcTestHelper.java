package be.sgerard.test.i18n.support;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/**
 * @author Sebastien Gerard
 */
@Component
public class AsyncMockMvcTestHelper {

    private final MockMvc mockMvc;

    public AsyncMockMvcTestHelper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public AsyncRequestAsserter perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        final ResultActions perform = this.mockMvc.perform(requestBuilder);

        return new AsyncRequestAsserter(perform);
    }

    public final class AsyncRequestAsserter {

        private final ResultActions resultActions;

        public AsyncRequestAsserter(ResultActions resultActions) {
            this.resultActions = resultActions;
        }

        public AsyncStartedAsserter andExpectStarted() throws Exception {
            resultActions.andExpect(request().asyncStarted());

            return new AsyncStartedAsserter(resultActions);
        }
    }

    public final class AsyncStartedAsserter {

        private final ResultActions resultActions;

        public AsyncStartedAsserter(ResultActions resultActions) {
            this.resultActions = resultActions;
        }

        public ResultActions andWaitResult() throws Exception {
            return mockMvc.perform(asyncDispatch(resultActions.andReturn()));
        }
    }
}

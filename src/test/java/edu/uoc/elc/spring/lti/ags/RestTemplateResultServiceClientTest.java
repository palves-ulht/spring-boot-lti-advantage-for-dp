package edu.uoc.elc.spring.lti.ags;

import edu.uoc.elc.Config;
import edu.uoc.elc.lti.tool.ResourceLink;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.ToolDefinition;
import edu.uoc.elc.lti.tool.oidc.InMemoryOIDCLaunchSession;
import edu.uoc.elc.spring.lti.tool.ToolDefinitionBean;
import edu.uoc.elc.spring.lti.security.LTIAccessTokenProvider;
import edu.uoc.elc.spring.lti.tool.ToolDefinitionFactory;
import edu.uoc.lti.ags.LineItem;
import edu.uoc.lti.ags.Result;
import edu.uoc.lti.claims.ClaimAccessor;
import edu.uoc.lti.oidc.OIDCLaunchSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.MethodNotAllowedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author xaracil@uoc.edu
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Config.class})
public class RestTemplateResultServiceClientTest {

	@Autowired
	private ToolDefinitionBean toolDefinitionBean;

	@Autowired
	private ClaimAccessor claimAccessor;

	@Autowired
	private String lineItemsUri;

	private RestTemplateLineItemServiceClient lineItemServiceClient;
	private LineItem lineItem;

	// subject under test
	private RestTemplateResultServiceClient sut;

	@Before
	public void setUp() throws URISyntaxException {
		Assert.assertNotNull(toolDefinitionBean);
		OIDCLaunchSession launchSession = new InMemoryOIDCLaunchSession();
		final ToolDefinition toolDefinition = ToolDefinitionFactory.from(toolDefinitionBean);
		final Tool tool = new Tool(toolDefinition,
						claimAccessor,
						launchSession,
						toolDefinitionBean.getBuilders());
		Assert.assertNotNull(tool);

		// spy tool
		Tool spy = Mockito.spy(tool);
		Mockito.when(spy.isValid()).thenReturn(true);
		Mockito.when(spy.getResourceLink()).thenReturn(new ResourceLink());

		final LTIAccessTokenProvider ltiAccessTokenProvider = new LTIAccessTokenProvider(spy);
		RestTemplateFactory restTemplateFactory = new TestRestTemplateFactory();
		RestTemplateLineItemServiceClientFactory lineItemServiceClientFactory = new RestTemplateLineItemServiceClientFactory(restTemplateFactory);
		this.lineItemServiceClient = lineItemServiceClientFactory.of(ltiAccessTokenProvider);
		this.lineItemServiceClient.setServiceUri(new URI(lineItemsUri));

		RestTemplateResultServiceClientFactory factory = new RestTemplateResultServiceClientFactory(restTemplateFactory);
		this.sut = factory.of(ltiAccessTokenProvider);
		Assert.assertNotNull(sut);

		// create line item
		this.lineItem = createAndAssertNewLineItem();

	}

	private static final String TEST_LABEL = "Test Label";
	private static final String TEST_TAG = "test";
	private static final Double TEST_SCORE_MAXIMUM = 1.0;

	@After
	public void tearDown() {
		try {
			this.lineItemServiceClient.deleteLineItem(lineItem.getId());
		} catch (MethodNotAllowedException ignored) {
		}
	}

	@Test
	public void getResults() {
		final List<Result> lineItemResults = this.sut.getLineItemResults(lineItem.getId(), null, null, null);
		Assert.assertNotNull(lineItemResults);
	}

	private LineItem lineItem(String label, double scoreMaximum) {
		LineItem lineItem = new LineItem();
		lineItem.setLabel(label);
		lineItem.setScoreMaximum(scoreMaximum);
		lineItem.setTag(TEST_TAG);
		return lineItem;
	}

	private LineItem createAndAssertNewLineItem() {
		LineItem lineItem = lineItem(TEST_LABEL, TEST_SCORE_MAXIMUM);
		final LineItem newLineItem = this.lineItemServiceClient.createLineItem(lineItem);
		assertNewLineItem(lineItem, newLineItem);
		return newLineItem;
	}

	private void assertNewLineItem(LineItem expected, LineItem actual) {
		Assert.assertNotNull(actual);
		Assert.assertNotNull(actual.getId());
		assertEquals(expected, actual, false);
	}

	private void assertEquals(LineItem expected, LineItem actual, boolean assertId) {
		if (assertId) {
			Assert.assertEquals(expected.getId(), actual.getId());
		}
		Assert.assertEquals(expected.getLabel(), actual.getLabel());
		Assert.assertEquals(expected.getScoreMaximum(), actual.getScoreMaximum(), 0);
		Assert.assertEquals(expected.getTag(), actual.getTag());
		Assert.assertEquals(expected.getResourceId(), actual.getResourceId());
		Assert.assertEquals(expected.getResourceLinkId(), actual.getResourceLinkId());
		Assert.assertEquals(expected.getSubmission(), actual.getSubmission());
	}
}

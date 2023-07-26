package it.olly.meetingcreator;

import java.net.URL;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.OnlineMeeting;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

public class TestNewTeamsMeeting {
	private class MyAuthProvider implements IAuthenticationProvider {
		private String token;

		public MyAuthProvider(String token) {
			this.token = token;
		}

		public CompletableFuture<String> getAuthorizationTokenAsync(URL requestUrl) {
			return CompletableFuture.completedFuture(token);
		}
	}

//	  https://learn.microsoft.com/en-us/graph/api/application-post-onlinemeetings?view=graph-rest-1.0&tabs=http
//		  
//	  POST https://graph.microsoft.com/v1.0/me/onlineMeetings
//
//		{
//		  "allowAttendeeToEnableCamera": "Boolean",
//		  "allowAttendeeToEnableMic": "Boolean",
//		  "allowMeetingChat": {"@odata.type": "microsoft.graph.meetingChatMode"},
//		  "allowTeamworkReactions": "Boolean",
//		  "allowedPresenters": "String",
//		  "attendeeReport": "Stream",
//		  "audioConferencing": {"@odata.type": "microsoft.graph.audioConferencing"},
//		  "broadcastSettings": {"@odata.type": "microsoft.graph.broadcastSettings"},
//		  "chatInfo": {"@odata.type": "microsoft.graph.chatInfo"},
//		  "creationDateTime": "String (timestamp)",
//		  "endDateTime": "String (timestamp)",
//		  "id": "String (identifier)",
//		  "isBroadcast": "Boolean",
//		  "isEntryExitAnnounced": "Boolean",
//		  "joinInformation": {"@odata.type": "microsoft.graph.itemBody"},
//		  "joinMeetingIdSettings": {"@odata.type": "microsoft.graph.joinMeetingIdSettings"},
//		  "joinWebUrl": "String",
//		  "lobbyBypassSettings": {"@odata.type": "microsoft.graph.lobbyBypassSettings"},
//		  "participants": {"@odata.type": "microsoft.graph.meetingParticipants"},
//		  "recordAutomatically": "Boolean",
//		  "startDateTime": "String (timestamp)",
//		  "subject": "String",
//		  "videoTeleconferenceId": "String",
//		}
//
//		get accessToken from "https://developer.microsoft.com/en-us/graph/graph-explorer"
//		user must have "OnlineMeetings.ReadWrite" permission

	@Test
	void testInitMeeting() throws ParseException {
		System.out.println("testing MSTeams meetiong creation...");
		String accessToken = "ACCESS_TOKEN_HERE";

		System.out.println("test connection");
		GraphServiceClient<?> graphClient = GraphServiceClient.builder()
				.authenticationProvider(new MyAuthProvider(accessToken)).buildClient();

		User user = graphClient.me().buildRequest().get();
		System.out.println("me: " + user.displayName);

		OnlineMeeting onlineMeeting = new OnlineMeeting();
		onlineMeeting.startDateTime = OffsetDateTime.now();
		onlineMeeting.endDateTime = OffsetDateTime.now().plusMinutes(15);
		onlineMeeting.subject = "Olly Meeting " + System.currentTimeMillis();
		// TODO: enable audio conferencing!!!

		OnlineMeeting meetingCreated = graphClient.me().onlineMeetings().buildRequest().post(onlineMeeting);
		System.out.println("Meeting created:");
		System.out.println("- id: " + meetingCreated.id);
		System.out.println("- joinWebUrl: " + meetingCreated.joinWebUrl);
		System.out.println("- subject: " + meetingCreated.subject);
		if (meetingCreated.audioConferencing != null) {
			System.out.println("- audioConferencing.conferenceId: " + meetingCreated.audioConferencing.conferenceId);
			System.out.println("- audioConferencing.tollNumbers: " + meetingCreated.audioConferencing.tollNumbers);
			System.out.println(
					"- audioConferencing.tollFreeNumbers: " + meetingCreated.audioConferencing.tollFreeNumbers);
		}

		System.out.println("testing MSTeams meetiong creation - done");
	}

}

# Project Setup Guide

To run the application successfully, follow the steps below to obtain the required tokens:

## 1. Slack Token

- Go to [Slack API](https://api.slack.com/apps).
- Create a new app, name it, and select the workspace (e.g., JetBrains).
- Navigate to **OAuth and Permissions** > **User Token Scopes** and select all the necessary permissions.
- Proceed to **Install App** and click **Request to Install**. Wait for workspace admins to approve the app.
- After approval (may take some time), the token will be visible in the **Install App** or **OAuth** tab.

### Required Slack Token Scopes:

- `channels:history`: View messages and content in user's public channels.
- `groups:history`: View messages and content in user's private channels.
- `reactions:read`: View emoji reactions in user's channels and conversations.
- `users.profile:read`: View profile details about people in the workspace.
- `users:read`: View people in the workspace.
- `users:read.email`: View email addresses of people in the workspace.

## 2. Space Token

- Generate a Space token in your Space profile > **Preferences** > **Personal Tokens**.

### Required Space Token Permissions:

- `Members`: View member profiles and view member profile basic info.
- `Member Teams`: View memberships.
- `Teams`: View teams.


## 3.Use these tokens:

Add these tokens to the Run configuration in your project under the following environment variables
- `SLACK_BOT_TOKEN`
- `SPACE_TOKEN`

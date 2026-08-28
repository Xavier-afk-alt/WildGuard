# WildGuard
很大风
=================================
# WildGuard

WildGuard is an Android application project developed using **Android Studio** and **Kotlin with Jetpack Compose**.

This README provides step-by-step instructions for contributors, especially **first-time users**, to correctly set up the project, connect to GitHub, run the application, and manage code changes.

---

# IMPORTANT REMINDERS

Before starting, please make sure that:

* Android Studio is installed and updated to a recent stable version.
* A GitHub account has already been created.
* You have been added as a collaborator to the WildGuard GitHub repository.
* You have a stable internet connection for Gradle synchronization and GitHub operations.
* You do not manually copy project files from another person's computer into your project folder unless necessary.
* Always use GitHub Pull and Push to synchronize project changes whenever possible.

> **Important:** First-time users should complete all setup procedures below before starting any coding work.

---

# TABLE OF CONTENTS

1. [First-Time Setup](#1-first-time-setup)
2. [Connect Android Studio to GitHub](#2-connect-android-studio-to-github)
3. [Clone the WildGuard Repository](#3-clone-the-wildguard-repository)
4. [Open and Configure the Project](#4-open-and-configure-the-project)
5. [Gradle Sync](#5-gradle-sync)
6. [Run the Application](#6-run-the-application)
7. [Daily Workflow Before Coding](#7-daily-workflow-before-coding)
8. [Making Changes to the Project](#8-making-changes-to-the-project)
9. [Commit and Push Changes](#9-commit-and-push-changes)
10. [Pull the Latest Changes](#10-pull-the-latest-changes)
11. [Checking Changes on GitHub](#11-checking-changes-on-github)
12. [Common Problems and Solutions](#12-common-problems-and-solutions)
13. [Important Git Rules](#13-important-git-rules)
14. [Debug Issues Reference](#14-debug-issues-reference)

---

# 1. FIRST-TIME SETUP

Before cloning and opening the project, make sure the required software is installed.

## Step 1: Install Android Studio

Install Android Studio on your computer if it has not been installed.

After installation:

1. Open Android Studio.
2. Check whether Android Studio requires an update.
3. If an update is available, update Android Studio if possible.
4. Restart Android Studio after the installation or update is completed.

It is recommended that all contributors use a reasonably recent version of Android Studio to reduce compatibility issues.

---

## Step 2: Install the Required Android SDK

Open Android Studio.

Navigate to:

```text
File > Settings
```

Then navigate to:

```text
Languages & Frameworks > Android SDK
```

Check whether the required Android SDK is installed.

If the project shows an error related to `compileSdk`, `targetSdk`, or a missing SDK, install the SDK version required by the project.

After installation:

1. Click `Apply`.
2. Wait for the SDK installation to finish.
3. Click `OK`.

---

## Step 3: Check the JDK Configuration

The project may require a compatible Java Development Kit version.

In Android Studio, navigate to:

```text
File > Settings > Build, Execution, Deployment > Build Tools > Gradle
```

Check the **Gradle JDK** setting.

If the project cannot build because of a JDK compatibility issue, use the JDK version supported by the current Android Studio and project configuration.

After changing the JDK:

1. Click `Apply`.
2. Click `OK`.
3. Perform a Gradle Sync.

---

# 2. CONNECT ANDROID STUDIO TO GITHUB

Before cloning, committing, or pushing code, connect Android Studio to your GitHub account.

## Step 1: Open Settings

In Android Studio, navigate to:

```text
File > Settings
```

---

## Step 2: Open GitHub Settings

Navigate to:

```text
Version Control > GitHub
```

---

## Step 3: Log In to GitHub

Click:

```text
Add Account
```

Then select the available GitHub login method.

Log in using your GitHub account and authorize Android Studio if required.

After successfully logging in:

1. Confirm that your GitHub account is displayed in the GitHub settings.
2. Click `Apply`.
3. Click `OK`.

---

# 3. CLONE THE WILDGUARD REPOSITORY

> **Important:** First-time users should clone the project from GitHub instead of manually copying project files.

## Step 1: Obtain the Repository URL

Go to the WildGuard GitHub repository.

Click:

```text
Code
```

Then copy the repository URL using HTTPS.

The repository URL should look similar to:

```text
https://github.com/USERNAME/REPOSITORY.git
```

Replace the example above with the actual WildGuard repository URL.

---

## Step 2: Clone the Repository in Android Studio

From the Android Studio welcome screen, click:

```text
Get from VCS
```

If Android Studio is already open with another project:

```text
File > New > Project from Version Control
```

---

## Step 3: Select Git

Select:

```text
Git
```

---

## Step 4: Paste the Repository URL

Paste the WildGuard repository URL into the URL field.

Choose a location on your computer where the project should be stored.

For example:

```text
C:\Users\YourName\AndroidStudioProjects\
```

Then click:

```text
Clone
```

---

## Step 5: Wait for the Cloning Process

Android Studio will download the project files from GitHub.

Wait until the cloning process is completed.

Do not close Android Studio while the project is still being cloned.

After cloning is completed, Android Studio may ask whether you want to open the project.

Select:

```text
Yes
```

or:

```text
Open
```

---

# 4. OPEN AND CONFIGURE THE PROJECT

After cloning, Android Studio should open the WildGuard project automatically.

If it does not:

1. Open Android Studio.
2. Click:

```text
File > Open
```

3. Navigate to the folder where the WildGuard repository was cloned.
4. Select the main project folder.

For example:

```text
WildGuard
```

5. Click:

```text
OK
```

> Do not open an individual Kotlin file as the project. Open the main project folder.

---

# 5. GRADLE SYNC

After opening the project for the first time, Android Studio may automatically begin Gradle synchronization.

If synchronization does not start automatically, click:

```text
File > Sync Project with Gradle Files
```

You can also use the Gradle Sync option shown in the Android Studio toolbar when available.

---

## Wait for Synchronization

During the first synchronization:

* Android Studio may download dependencies.
* Gradle may download required files.
* The synchronization process may take several minutes depending on your internet connection.

Do not start modifying project configuration files while Gradle Sync is still running.

Wait until synchronization is completed successfully.

---

## If Gradle Sync Fails

Check the error message shown in:

```text
Build
```

or:

```text
Event Log
```

Common causes include:

* Missing Android SDK.
* Incorrect Gradle JDK.
* Internet connection problems.
* Incompatible Android Studio version.
* Missing dependencies.
* Incorrect project configuration.

Fix the reported issue before continuing.

---

# 6. RUN THE APPLICATION

After Gradle Sync is completed successfully, the application can be run using either an Android Emulator or a physical Android device.

---

## OPTION A: RUN USING AN ANDROID EMULATOR

### Step 1: Open Device Manager

In Android Studio, navigate to:

```text
Tools > Device Manager
```

---

### Step 2: Create a Virtual Device

Click:

```text
Create Device
```

Select an Android device model.

For example:

```text
Pixel 7
```

Then click:

```text
Next
```

---

### Step 3: Select a System Image

Choose an installed Android system image.

If the required system image is not installed, Android Studio will provide an option to download it.

After selecting the system image:

1. Click `Next`.
2. Review the configuration.
3. Click `Finish`.

---

### Step 4: Start the Emulator

Select the created virtual device.

Click the:

```text
Run
```

or:

```text
Play
```

button.

Wait for the Android Emulator to fully start.

---

### Step 5: Run WildGuard

At the top of Android Studio:

1. Select the WildGuard application configuration.
2. Select the running emulator.
3. Click the:

```text
Run ▶
```

button.

Wait for Android Studio to build and install the application.

---

## OPTION B: RUN USING A PHYSICAL ANDROID DEVICE

### Step 1: Enable Developer Options

On your Android device:

1. Open `Settings`.
2. Navigate to `About Phone`.
3. Find `Build Number`.
4. Tap the Build Number multiple times until Developer Options are enabled.

The exact location may be different depending on the Android device.

---

### Step 2: Enable USB Debugging

Navigate to:

```text
Settings > Developer Options
```

Enable:

```text
USB Debugging
```

---

### Step 3: Connect the Device

Connect the Android device to your computer using a USB cable.

If the device asks:

```text
Allow USB Debugging?
```

Select:

```text
Allow
```

---

### Step 4: Run the Application

In Android Studio:

1. Select your physical Android device from the device list.
2. Click:

```text
Run ▶
```

Android Studio will build and install the application on the device.

---

# 7. DAILY WORKFLOW BEFORE CODING

> **Important:** Always check for the latest changes before starting new work.

This helps reduce the possibility of editing outdated files.

Before starting to code:

1. Open the WildGuard project.
2. Wait for Android Studio to finish loading.
3. Check whether Gradle Sync is required.
4. Pull the latest changes from GitHub.

---

## Pull the Latest Changes

Navigate to:

```text
Git > Pull
```

Select the correct remote and branch if Android Studio asks.

Then click:

```text
Pull
```

Wait until the pull process is completed.

If successful, Android Studio may show a message indicating that the project is already up to date or that new changes have been downloaded.

---

## After Pulling

After successfully pulling:

1. Wait for any required Gradle Sync.
2. Check whether there are any errors.
3. Run the application if necessary.
4. Confirm that the project still works before starting your own changes.

Only then begin coding.

---

# 8. MAKING CHANGES TO THE PROJECT

When working on the project:

* Modify only the files related to your assigned task whenever possible.
* Avoid changing unrelated files.
* Avoid deleting files unless you understand their purpose.
* Avoid changing Gradle configuration unnecessarily.
* Test your changes before committing.
* Make sure the application can still build successfully.

Before committing:

1. Save all changes.
2. Run the application.
3. Test the feature or modification.
4. Check for errors in Android Studio.
5. Confirm that the changes are working as expected.

---

# 9. COMMIT AND PUSH CHANGES

> **Important:** Always commit and push your completed changes before closing the project.

---

## Step 1: Open the Commit Window

In Android Studio, click:

```text
Git > Commit
```

You may also use the Commit button if it is displayed in the Android Studio interface.

The Commit window will display the files that have been modified.

---

## Step 2: Review the Changed Files

Check the list of changed files carefully.

Make sure that:

* Your intended code changes are included.
* Unrelated files are not accidentally included.
* Temporary or unnecessary files are not included.

Select or tick the files that should be committed.

> Do not blindly select every file without checking the changes.

---

## Step 3: Review the Code Changes

Click on the changed files to review the differences.

Check whether:

* The correct code was modified.
* No important code was accidentally deleted.
* No debugging code should be removed.
* No unrelated changes are being committed.

---

## Step 4: Write a Clear Commit Message

Write a meaningful commit message describing what was changed.

### Good examples

```text
Add inventory quick access feature
```

```text
Fix animal selector scrolling behavior
```

```text
Update achievement reward display
```

```text
Fix navigation between reward and redeem pages
```

### Avoid unclear commit messages

```text
Update
```

```text
Fix
```

```text
Changes
```

```text
Done
```

A commit message should make it easy for other contributors to understand the purpose of the changes.

---

## Step 5: Commit and Push

After reviewing the files and writing the commit message, click:

```text
Commit and Push
```

Android Studio will first create a local Git commit and then attempt to upload the changes to GitHub.

---

## Step 6: Handle Warnings Carefully

Android Studio may display warnings before committing.

Review the warning.

If the warning is not related to a serious problem and you have reviewed your changes, you may select:

```text
Commit Anyway and Push
```

However, do not automatically ignore warnings without checking them.

---

## Step 7: Wait for the Push Process

Wait for the Git operation to complete.

Do not close Android Studio while the push is still in progress.

A successful notification should appear when the changes have been pushed successfully.

---

# 10. PULL THE LATEST CHANGES

Sometimes another contributor may have pushed changes while you were working.

Before starting a new task, always pull the latest version.

Navigate to:

```text
Git > Pull
```

Then wait for the process to complete.

---

## If the Project Is Already Up to Date

You may receive a message similar to:

```text
Already up to date
```

This means your local project already contains the latest changes from the selected remote branch.

You can continue working.

---

## If New Changes Are Downloaded

If another contributor has pushed new changes:

1. Wait for the pull process to finish.
2. Allow Gradle Sync if required.
3. Check whether any errors appear.
4. Run the application if necessary.
5. Continue working only after confirming that the project is functioning correctly.

---

# 11. CHECKING CHANGES ON GITHUB

After successfully pushing your changes:

1. Open the WildGuard repository on GitHub.
2. Refresh the page.
3. Check the commit history.
4. Confirm that your latest commit message appears.
5. Open the changed files if necessary.
6. Confirm that the latest code is available on GitHub.

This provides an additional confirmation that the push was successful.

---

# 12. COMMON PROBLEMS AND SOLUTIONS

## Problem 1: Project Does Not Open Correctly

Make sure that you opened the main project folder.

Use:

```text
File > Open
```

Then select the WildGuard project folder.

Do not open only an individual Kotlin file.

---

## Problem 2: Gradle Sync Failed

Possible causes:

* Missing Android SDK.
* Incorrect JDK configuration.
* Internet connection problems.
* Missing dependencies.
* Android Studio compatibility issues.

Check the error message shown in Android Studio and fix the specific issue before continuing.

---

## Problem 3: Cannot See the Latest Code

Before assuming that the code is missing:

1. Check which branch you are currently using.
2. Confirm that you are pulling from the correct branch.
3. Use:

```text
Git > Pull
```

4. Wait for the process to complete.
5. Check the files again.

---

## Problem 4: Cannot Push Changes

Possible reasons include:

* Your local branch is outdated.
* Another contributor pushed changes first.
* There is a merge conflict.
* You do not have permission to push to the repository.
* Your internet connection is unavailable.

First, do not repeatedly click Push.

Check the error message.

If your branch is outdated:

1. Save your work.
2. Pull the latest changes.
3. Resolve any conflicts if necessary.
4. Test the project.
5. Commit and push again.

---

## Problem 5: Merge Conflict

A merge conflict may occur when multiple contributors modify the same part of a file.

Do not randomly delete code to remove the conflict.

Instead:

1. Identify the conflicting file.
2. Review both versions of the code.
3. Determine which changes should be kept.
4. Resolve the conflict carefully.
5. Test the application.
6. Commit the resolved version.

If you are unsure how to resolve the conflict, discuss it with the contributor who modified the same file before continuing.

---

## Problem 6: The Emulator Runs an Old Version of the Application

Try the following:

1. Stop the currently running application.
2. Confirm that the correct WildGuard project is open.
3. Select the correct run configuration.
4. Click:

```text
Build > Clean Project
```

5. Then click:

```text
Build > Rebuild Project
```

6. Run the application again.

If the problem continues, uninstall the old application from the emulator and run the project again.

---

## Problem 7: Red Errors Appear After Pulling Changes

After pulling new changes:

1. Wait for Gradle Sync to complete.
2. Check whether new dependencies are being downloaded.
3. Try:

```text
File > Sync Project with Gradle Files
```

4. Check whether the required SDK is installed.
5. Check the Gradle JDK configuration.

Do not immediately modify project files before identifying the actual cause of the error.

---

# 13. IMPORTANT GIT RULES

To reduce GitHub and project synchronization problems, please follow these rules.

## Rule 1: Pull Before Starting

Before starting new work:

```text
Git > Pull
```

Always make sure you are working with the latest project version.

---

## Rule 2: Test Before Committing

Before committing:

* Build the project.
* Run the application.
* Test your changes.
* Check for obvious errors.

Do not commit code that you know is broken unless the team has specifically agreed to do so.

---

## Rule 3: Use Clear Commit Messages

Your commit message should describe what you changed.

Example:

```text
Add up and down indicators for vertical scrolling
```

Instead of:

```text
Update
```

---

## Rule 4: Review Files Before Committing

Do not automatically commit every changed file.

Check the list carefully.

Only commit files related to your intended changes.

---

## Rule 5: Do Not Force Push Unless You Understand the Consequences

Avoid using:

```text
Force Push
```

unless you fully understand why it is necessary.

Force pushing can overwrite changes on the remote repository.

---

## Rule 6: Do Not Delete Other Contributors' Code Without Confirmation

If you find code that appears unused or unnecessary, confirm its purpose before deleting it.

Another contributor may currently be working on or depending on that code.

---

## Rule 7: Communicate When Editing the Same Feature

If multiple contributors need to modify the same feature or file, communicate with each other before making major changes.

This can reduce merge conflicts and duplicated work.

---

# RECOMMENDED WORKFLOW SUMMARY

For normal daily development, follow this sequence:

```text
1. Open Android Studio
        ↓
2. Open the WildGuard project
        ↓
3. Pull the latest changes from GitHub
        ↓
4. Wait for Gradle Sync
        ↓
5. Run the application if necessary
        ↓
6. Start coding
        ↓
7. Test the changes
        ↓
8. Review modified files
        ↓
9. Write a clear commit message
        ↓
10. Commit and Push
        ↓
11. Confirm the push was successful
        ↓
12. Check GitHub if necessary
        ↓
13. Safely close the project
```

---

# FINAL CHECKLIST BEFORE CLOSING ANDROID STUDIO

Before closing the project, confirm the following:

* [ ] My code changes have been saved.
* [ ] The project builds successfully.
* [ ] I tested my changes.
* [ ] I reviewed the modified files.
* [ ] I wrote a meaningful commit message.
* [ ] My changes were committed.
* [ ] My changes were pushed successfully.
* [ ] GitHub contains my latest commit.
* [ ] There are no unfinished Git operations.

If all items are completed, the Android Studio project can be safely closed.

---

# FOR FIRST-TIME USERS

If this is your first time joining the project, follow this order:

```text
1. Install or update Android Studio.
2. Create and log in to your GitHub account.
3. Make sure you have access to the WildGuard repository.
4. Connect Android Studio to GitHub.
5. Clone the WildGuard repository.
6. Open the main project folder.
7. Install any required Android SDK.
8. Configure the correct Gradle JDK if necessary.
9. Perform Gradle Sync.
10. Fix any setup errors.
11. Create or start an Android Emulator, or connect a physical device.
12. Run the WildGuard application.
13. Confirm that the application works correctly.
14. Pull the latest changes before starting any coding task.
15. Start development.
```

> **Always remember:** Pull before starting, test before committing, review before pushing, and confirm that your changes are successfully uploaded before closing the project.

# 14. DEBUG ISSUES REFERENCE
If got issue to pull the project file from cloned repository that created previously, can refer this link for debug issues.
[https://chatgpt.com/share/6a91a1d1-3abc-83ec-85d7-b9b195209e44](url)


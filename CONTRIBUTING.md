# Contributing

[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![Semantic Versioning](https://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)
[![Gitter](https://badges.gitter.im/Syncleus/aparapi.svg)](https://gitter.im/Syncleus/aparapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

When contributing to this repository, it is usually a good idea to first discuss the change you
wish to make via issue, email, or any other method with the owners of this repository before
making a change. This could potentially save a lot of wasted hours.

Please note we have a code of conduct, please follow it in all your interactions with the project.

## Development

### Commit Message Format

Starting version 1.3.3 and later all commits on the Syncleus Aparapi repository follow the
[Conventional Changelog standard](https://github.com/conventional-changelog/conventional-changelog-eslint/blob/master/convention.md).
It is a very simple format so you can still write commit messages by hand. However it is
highly recommended developers install [Commitizen](https://commitizen.github.io/cz-cli/),
it extends the git command and will make writing commit messages a breeze. All the Aparapi
repositories are configured with local Commitizen configuration scripts.

Getting Commitizen installed is usually trivial, just install it via npm. You will also
need to install the cz-customizable adapter which the Aparapi repository is configured
to use.

```bash

npm install -g commitizen@2.8.6 cz-customizable@4.0.0
```

Below is an example of Commitizen in action. It replaces your usual `git commit` command
with `git cz` instead. The new command takes all the same arguments however it leads you
through an interactive process to generate the commit message.

![Commitizen friendly](http://aparapi.com/images/commitizen.gif)

Commit messages are used to automatically generate our changelogs, and to ensure
commits are searchable in a useful way. So please use the Commitizen tool and adhere to
the commit message standard or else we cannot accept Pull Requests without editing
them first.

Below is an example of a properly formated commit message.

```
chore(Commitizen): Made repository Commitizen friendly.

Added standard Commitizen configuration files to the repo along with all the custom rules.

ISSUES CLOSED: #31
```

### Pull Request Process

1. Ensure that install or build dependencies do not appear in any commits in your code branch. 
2. Ensure all commit messages follow the [Conventional Changelog](https://github.com/conventional-changelog/conventional-changelog-eslint/blob/master/convention.md)
   standard explained earlier.
3. Update the CONTRIBUTORS.md file to add your name to it if it isn't already there (one entry
   per person).
4. Adjust the project version to the new version that this Pull Request would represent. The
   versioning scheme we use is [Semantic Versioning](http://semver.org/).
5. Your pull request will either be approved or feedback will be given on what needs to be
   fixed to get approval. We usually review and comment on Pull Requests within 48 hours.

### Making a Release

Only administrators with privilages to push to the Aparapi Maven Central account can deploy releases. If this isn't you
then you can just skip this section.

First ensure the package is prepared for the release process:

* Make sure any references to the version number in the readme is updated
  * Version number in dependency maven snippet.
  * Add new version to javadoc version list.
* Ensure that none of the dependencies used are snapshots.
* Update the changelog file.
* Check that all Aparapi libraries used as dependencies point to the latest version.

Next lets take a few steps to do the actual release:

1.  Update everything listed above. Do **not** drop the package version's `-SNAPSHOT` suffix in master.
2.  Create a release branch, but make sure never to push this branch to the server: `git checkout -b release`.
3.  Update the README.md again to ensure travis badge and javadoc badge point to static tag and not latest.
4.  Drop the `-SNAPSHOT` suffix from the package version.
5.  Commit the current changes using a generic commit message such as `build(release): version 1.2.3`.
6.  Fully test the software before deploying, run all tests and install locally to test against the examples package.
    You can install the package locally with `mvn clean install`.
7.  Once satisfied the package is stable deploy it to maven central by executing `mvn -P sign clean package deploy`.
8.  If deployment was successful then create a new tag for the current version with the following command:
    `git tag -a v1.2.3 -m "Version 1.2.3"`.
9.  Push the newly created tags to the server: `git push origin v1.2.3:v1.2.3`.
10. Go to Github and go to the release. Update the description with the changelog for the version and upload
    all the artifacts in the target folder.
10. Checkout master again and then delete the release branch: `git branch -D release`.
11. Bump the snapshot version of the package to the next expected version, commiting the changes and pushing.
12. Deploy the new snapshot to the snapshot repository (no need to sign): `mvn clean deploy`.

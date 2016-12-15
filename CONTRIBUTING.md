# Contributing

[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![Semantic Versioning](https://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)

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

npm i -g commitizen cz-customizable
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

## Code of Conduct

### Our Pledge

In the interest of fostering an open and welcoming environment, we as
contributors and maintainers pledge to making participation in our project and
our community a harassment-free experience for everyone, regardless of age, body
size, disability, ethnicity, gender identity and expression, level of experience,
nationality, personal appearance, race, religion, or sexual identity and
orientation.

### Our Standards

Examples of behavior that contributes to creating a positive environment
include:

* Using welcoming and inclusive language
* Being respectful of differing viewpoints and experiences
* Gracefully accepting constructive criticism
* Focusing on what is best for the community
* Showing empathy towards other community members

Examples of unacceptable behavior by participants include:

* Unwelcomed sexual attention or advances.
* Derogatory comments about a persons appearance, race, or sexual orientation.
* Public or private harassment
* Publishing others' private information, such as a physical or electronic
  address, without explicit permission

### Our Responsibilities

Project maintainers are responsible for clarifying the standards of acceptable
behavior and are expected to take appropriate and fair corrective action in
response to any instances of unacceptable behavior.

Project maintainers have the right and responsibility to remove, edit, or
reject comments, commits, code, wiki edits, issues, and other contributions
that are not aligned to this Code of Conduct, or to ban temporarily or
permanently any contributor for other behaviors that they deem inappropriate,
threatening, offensive, or harmful.

### Scope

This Code of Conduct applies both within project spaces and in public spaces
when an individual is representing the project or its community. Examples of
representing a project or community include using an official project e-mail
address, posting via an official social media account, or acting as an appointed
representative at an online or offline event. Representation of a project may be
further defined and clarified by project maintainers.

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be
reported by contacting the project team at [INSERT EMAIL ADDRESS]. All
complaints will be reviewed and investigated and will result in a response that
is deemed necessary and appropriate to the circumstances. The project team is
obligated to maintain confidentiality with regard to the reporter of an incident.
Further details of specific enforcement policies may be posted separately.

Project maintainers who do not follow or enforce the Code of Conduct in good
faith may face temporary or permanent repercussions as determined by other
members of the project's leadership.

### Attribution

This Code of Conduct is adapted from the [Contributor Covenant][homepage], version 1.4,
available at [http://contributor-covenant.org/version/1/4][version]

[homepage]: http://contributor-covenant.org
[version]: http://contributor-covenant.org/version/1/4/

'use strict';

module.exports = {

  types: [
    {value: 'feat',     name: 'feat:     A new feature'},
    {value: 'fix',      name: 'fix:      A bug fix'},
    {value: 'docs',     name: 'docs:     Documentation only changes'},
    {value: 'style',    name: 'style:    Changes that do not affect the meaning of the code\n            (white-space, formatting, etc)'},
    {value: 'refactor', name: 'refactor: A code change that neither fixes a bug nor adds a feature'},
    {value: 'perf',     name: 'perf:     A code change that improves performance'},
    {value: 'test',     name: 'test:     Adding missing tests or correcting existing tests'},
    {value: 'build',    name: 'build:    Changes that affect the build system or external dependencies (example scopes: maven, gradle, npm, gulp)'},
    {value: 'ci',       name: 'ci:       Changes to our CI configuration files and scripts (example scopes: Travis, Circle, BrowserStack, SauceLabs)'},
    {value: 'chore',    name: 'chore:    Other changes that dont modify src or test files'},
    {value: 'revert',   name: 'revert:   Reverts a previous commit'}
  ],

  scopes: [
    {name: 'jni'},
    {name: 'internal'},
    {name: 'swing'},    
    {name: 'opencl'}
  ],

  scopeOverrides: {
    build: [
      {name: 'dependencies'},
      {name: 'versioning'},
      {name: 'release'},
      {name: 'build plugin'}
    ],
    ci: [
      {name: 'travis'}
    ],
    chore: [
      {name: 'commitizen'},
      {name: 'editorconfig'},
      {name: 'git'}
    ],
    docs: [
      {name: 'javadocs'},
      {name: 'repo'},
      {name: 'maven'}
    ]
  },

  allowCustomScopes: true,
  allowBreakingChanges: ['feat', 'fix', 'docs', 'style', 'refactor', 'perf', 'test', 'build', 'ci', 'chore', 'revert']

};

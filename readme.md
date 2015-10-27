## Introduction

This package implements a simple DSL for executing shell commands directly from Scala. It's especially handy if you spend a lot of time in the repl and are reluctant to start up a terminal.

## Usage

    // Run Pipes
    "ls" | "grep -i pdf" | "wc -l" rp;
    // Run If the previous command exit OK
    "mkdir tmp" && "touch tmp/file1 tmp/file2" && "cp file3 tmp/" rio;
    // Run if the previous command exit with Error
    "mkdir tmp" || "mkdir ../tmp" || "mkdir ../../tmp" rie;
    // The `;` at the end of these lines are important

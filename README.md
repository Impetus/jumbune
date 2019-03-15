jumbune [![Build Status](https://travis-ci.org/Impetus/jumbune.svg?branch=master)](https://travis-ci.org/Impetus/jumbune)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FImpetus%2Fjumbune.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FImpetus%2Fjumbune?ref=badge_shield)
=======

## Synopsis

Jumbune is an open-source product built for analyzing Hadoop cluster and MapReduce jobs. It provides development & administrative insights of Hadoop based analytical solutions. It enables user to Debug, Profile, Monitor & Validate analytical solutions hosted on decoupled clusters.

## Website
http://jumbune.org

## Issue Tracker
http://jumbune.org/jira/browse/JUM

## Building

Jumbune can be build with maven

- To build for a Yarn Hadoop cluster

 `mvn clean install -P yarn`

- To build for a non Yarn Hadoop cluster

 `mvn clean install`

## Installation

_Detailed installation guide can be found at http://bit.ly/1kBG4Qo_

_Deployment Planning guide at http://bit.ly/1oiXGk2_

- Deploying Jumbune

`java -jar <location of the distribution build jar>`

- Deploying Jumbune (in verbose mode)

`java -jar <location of the distribution build jar> -verbose`

- Running Jumbune Agent

`java -jar <jumbune agent jar> <port> <|verbose>`

- Running Jumbune

`./startWeb`

or

`./runCli`

## Docker Image (Jumbune + Apache YARN )

- Building from root of the checked out repository
`$ sudo docker build –t = "jumbune/pseudo-distributed:1.5.1" .`

- Building from the github.com repository
`$ sudo docker build –t = "jumbune/pseudo-distributed:1.5.1" github.com/Impetus/jumbune/`

- Getting the automated build from docker registry
`https://registry.hub.docker.com/u/jumbune/jumbune/`

- Running the built image
`$ docker run -d --name="jumbune" -h "jumbune-docker" -p 8080:8080 -p 5555 jumbune/pseudo-distributed:1.5.1`

## Code Examples

Code examples are packages inside the distribution,

- For Flow analyzer: BankDefaulters, ClickStreamAnalysis, USRegionPortouts
- For Profilng: MovieRating
- For Data Validation - script

## Documentation
- Quick Start Guide: http://bit.ly/1mY9qWe
- Installation Guide: http://bit.ly/1kBG4Qo
- Release Notes: http://bit.ly/TkWSj8
- Architecture Guide: http://bit.ly/UgJayB
- Deployment Planning Guide: http://bit.ly/1oiXGk2
- Administration Guide: http://bit.ly/1uq5LVV
- Troubleshooting Guide: http://bit.ly/1pXEYOd
- Getting Involved Guide: http://bit.ly/1i7XjDn

## License

Jumbune is licensed under LGPLv3 license


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FImpetus%2Fjumbune.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FImpetus%2Fjumbune?ref=badge_large)
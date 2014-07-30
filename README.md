# feed-ontology-closure

This tool can be used to assert all redundant subclass relationships for an input ontology. For example, given:

```
A subClassOf B
B subClassOf C
```

a redundant statement is added:

`A subClassOf C`

This is useful for preprocessing an ontology for input to systems with no reasoning capabilities.

Additionally, this tool redundantly asserts the partonomy graph as class relationships, by means of annotation assertions. For example, if:

`A subClassOf (part_of some D)`

then an annotation directly linking the classes is added, using an annotation property with IRI equal to the part\_of property's IRI + "\_some":

`A part_of_some D`

## Installation and running
Note: you must have a working Java installation to run this tool.
* Download the latest release from https://github.com/NESCent/feed-ontology-closure/releases
* Unzip the download, then `cd feed-ontology-closure-<version>/bin`.
* Run: `./feed-ontology-closure input_file.owl output_file.owl`

If your ontology is large you may need to increase the memory allocated to the tool. You can do this by passing the `-mem` argument, with the desired number in megabytes: `./feed-ontology-closure -mem 2048 input_file.owl output_file.owl`

##Building
Building requires the Scala build tool, [sbt](http://www.scala-sbt.org). If you are running Mac OS X and use [Homebrew](http://brew.sh), then you can get sbt via `brew install sbt`.

To package the software for download, run `sbt universal:packageZipTarball`. You can compile and run the code directly within sbt like so:

`sbt "run input_file.owl output_file.owl"`

ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)


lazy val compilerOptions = Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-unchecked", "-Yno-adapted-args", "-Ywarn-dead-code",
  "-Ywarn-numeric-widen", "-Ywarn-value-discard", //  "-Xfatal-warnings",
  "-Xfuture", //  "-Xlint",
  "-Ywarn-unused-import", "-Yno-adapted-args"
  //  , "-Ymacro-debug-lite"
  //  , "-Ymacro-debug-verbose"
)

name := "Test TypeFactory"

version := "0.1-SNAPSHOT"

organization := "nl.ing.test"

ThisBuild / scalaVersion := "2.12.7"

val flinkVersion = "1.7.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided"
)

lazy val core = (project in file("core"))
  .settings(scalacOptions ++= compilerOptions,
    libraryDependencies ++= flinkDependencies
  )

lazy val job = (project in file("job"))
  .settings(scalacOptions ++= compilerOptions,
    libraryDependencies ++= flinkDependencies
  )
  .dependsOn(core)

lazy val root = (project in file(".")).
  aggregate(core, job)

assembly / mainClass := Some("nl.ing.test.TestTypeFactory")

// make run command include the provided dependencies
Compile / run := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false)

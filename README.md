# Using Gradle Managed Devices with own device farm

It is a demo project for "Using Gradle Managed Devices with own device farm" article on Bumble
Medium blog.

The project shows how to use Gradle Managed Devices with self-hosted emulators,
which might be hosted both locally and remotely.

The project has the following modules:

1. `plugin` contains a Gradle plugin that sets up Gradle Managed Devices.
2. `lib` is an Android library module with UI tests.
3. `app` is an Android application module with UI tests. Depends on `lib`.

## How to use

1. Launch 2 local emulators. They should use 5554 and 5556 ports by default.
2. Execute either `myDeviceCheck` or `multipleDevicesCheck`.
3. Verify that the tests run correctly, on single or multiple devices in parallel.

## License

```
Copyright 2022 Bumble

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

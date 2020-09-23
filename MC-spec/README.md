# MC-spec
The specification of the Mission Command game.

## License

© Copyright Benedict Adamson 2018-20.
 
![AGPLV3](https://www.gnu.org/graphics/agplv3-with-text-162x68.png)

MC is free software: you can redistribute it and/or modify
it under the terms of the
[GNU Affero General Public License](https://www.gnu.org/licenses/agpl.html)
as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MC is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with MC.  If not, see <https://www.gnu.org/licenses/>.

## Overview

This component provides specification documents of MC.

This component specifies MC using a BDD style, using the
[Gherkin](https://cucumber.io/docs/gherkin/)
language, for use with
[Cucumber](https://docs.cucumber.io/cucumber/).
The component uses [Maven](https://maven.apache.org/)
to package the Gherkin files (into a JAR), for use by other modules.

## Cucumber Tags

The BDD scenarios use Cucumber tags to indicate which components can be practically tested against which scenarios.
* `@back-end`: the back-end of the system can be practically tested against this scenario.
* `@front-end`: the front-end of the system can be practically tested against this scenario.
* `@integration`: the system as a whole can be practically tested against this scenario.

Here, *practically* does not mean only *is possible*, but also that the testing is *cost effective*.
In theory, the system as a whole could be tested against all the  requirements,
but that would be too expensive.
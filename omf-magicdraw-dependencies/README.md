# Magicdraw dependencies
This module only purpose is as a hack to allow sub-projects of omf-plugin to compile withouth having to download an instance of Magicdraw for each.

To import magicdraw dependencies, other subprojects must import this lib as compileOnly conf : 

``compileOnly project("omf-plugin:omf-magicdraw-dependencies")``

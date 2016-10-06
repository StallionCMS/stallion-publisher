#!/usr/bin/env python3
import os
from plumbum import local, FG
from plumbum.cmd import mvn, cat, chmod
import shutil


base = os.getcwd()
files = os.listdir(base + '/src/main/resources/sql/')
files.sort()
if files:
    last = files[-1].split('-')[0]
else:
    last = '00000'
noop_file = last + '-noop.mysql.js'
print('noop file ' + noop_file)
with open(base + '/src/test/resources/example_site/sql/' + noop_file, 'w') as f:
    f.write('\n\n\n')
mvn['compile', 'exec:java', '-Dexec.mainClass=io.stallion.publisher.MainRunner', '-Dexec.args=sql-generate -targetPath=/Users/pfitzsimmons/st/publisher/src/test/resources/example_site -devMode=true'] & FG
local['mv'][local.path().glob(base + '/src/test/resources/example_site/sql/*'), base + '/src/main/resources/sql/'] & FG
local['trash'][base + '/src/main/resources/sql/' + noop_file] & FG
files = os.listdir(base + '/src/main/resources/sql/')
files.sort()
print("SQL FILES: \n")
import json
print(json.dumps(files, indent=4))


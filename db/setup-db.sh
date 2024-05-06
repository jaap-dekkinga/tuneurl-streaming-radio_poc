#!/bin/bash
#
# BSD 3-Clause License
#
# Copyright (c) 2024, Teodoro M. Albon, <albonteddy@gmail.com>
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# 3. Neither the name of the copyright holder nor the names of its
#    contributors may be used to endorse or promote products derived from
#    this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
PARAM=$1

DBNAME=poctuneurl
USERNAME=root
DBRESET=poctuneurl-20240417-0413.sql
DBDUMP=poctuneurl-20240417-0413.sql

function die(){
  echo ""
  echo "ERROR: $1"
  echo ""
  exit 0
}

function usage(){
  echo ""
  echo "usage: $0 [reset : install : dump : restoredump]"
  echo "where: install - Install the initial SDK database: $DBNAME"
  echo "          dump - Save all records from database: $DBNAME to $DBDUMP"
  echo "   restoredump - Restore records from $DBDUMP of database: $DBNAME"
  exit 0
}

if [ "x$PASSWORD" = "x" ]; then
  die "Define PASSWORD and run again" ;

fi

if [ ! -f ./data.sql ]; then
  die "Missing ./data.sql" ;

fi

if [ ! -f ./init.sql ]; then
  die "Missing ./init.sql" ;

fi

if [ ! -f ./data-reset.sql ]; then
  die "Missing ./data-reset.sql" ;

fi

if [ "x$PARAM" = "xdump" ]; then

  rm -f $DBDUMP
  mysqldump --user=$USERNAME --password=$PASSWORD $DBNAME > $DBDUMP
  ls -vlt $DBDUMP
  exit 0
fi

if [ "x$PARAM" = "xreset" ]; then
DBDUMP=$DBRESET

  mysqladmin --user=$USERNAME --password=$PASSWORD drop $DBNAME
  mysql --user=$USERNAME --password=$PASSWORD < ./init.sql

  mysql --user=$USERNAME --password=$PASSWORD $DBNAME < $DBDUMP
  mysqlshow --user=$USERNAME --password=$PASSWORD
  exit 0

fi

if [ "x$PARAM" = "xrestoredump" ]; then

  if [ ! -f ./$DBDUMP ]; then
    die "Missing Dump file $DBDUMP" ;
  fi

  mysqladmin --user=$USERNAME --password=$PASSWORD drop $DBNAME
  mysql --user=$USERNAME --password=$PASSWORD < ./init.sql

  mysql --user=$USERNAME --password=$PASSWORD $DBNAME < $DBDUMP
  mysqlshow --user=$USERNAME --password=$PASSWORD
  exit 0
fi

if [ "x$PARAM" != "xinstall" ]; then
  usage ;
fi

# mysqladmin: [Warning] Using a password on the command line interface can be insecure.
# Dropping the database is potentially a very bad thing to do.
# Any data stored in the database will be destroyed.
#
# Do you really want to drop the 'magpieinventory' database [y/N] y
# Database "magpieinventory" dropped
# mysql: [Warning] Using a password on the command line interface can be insecure.
# mysql: [Warning] Using a password on the command line interface can be insecure.
# mysqlshow: [Warning] Using a password on the command line interface can be insecure.
# +--------------------+
# |     Databases      |
# +--------------------+
# | information_schema |
# | mysql              |
# | performance_schema |
# | sys                |
# | poctuneurl         |
# +--------------------+

mysqladmin --user=$USERNAME --password=$PASSWORD drop $DBNAME
mysql --user=$USERNAME --password=$PASSWORD < ./init.sql
mysql --user=$USERNAME --password=$PASSWORD < ./data.sql
mysqlshow --user=$USERNAME --password=$PASSWORD

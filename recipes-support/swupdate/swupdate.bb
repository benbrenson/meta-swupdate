# This software is a part of ISAR.
# Copyright (C) Mixed Mode GmbH 2017

inherit dpkg-cross

DEB_DEPENDS += "liblua5.2-dev libconfig-dev libjson-c-dev libcurl4-gnutls-dev"

URL="git://github.com/sbabic/swupdate.git"
BRANCH="master"
#SRCREV = "${BRANCH}"
TAG="2017.04"

SRC_DIR = "git"
SRC_URI += "${URL};branch=${BRANCH};tag=${TAG};protocol=https"


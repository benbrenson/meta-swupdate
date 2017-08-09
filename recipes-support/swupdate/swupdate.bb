DESCRIPTION = "Memory Technology Device Utilities. Utilities for manipulating memory technology devices, such as flash \
memory, Disk-On-Chip, or ROM.  Includes mkfs.jffs2, a tool to create \
JFFS2 (journaling flash file system) filesystems."
DESCRIPTION_DEV = " Memory Technology Device Utilities. Libraries and header files for developing."
LICENSE = "gpl2"

inherit dpkg debianize

DEPENDS += "mtd-utils"
DEB_DEPENDS += "liblua5.2-dev libconfig-dev libjson-c-dev libcurl4-gnutls-dev mtd-utils-dev libubootenv"

URL="git://github.com/sbabic/swupdate.git"
BRANCH="master"
#SRCREV = "${BRANCH}"
TAG="2017.04"

SRC_DIR = "git"
SRC_URI += "${URL};branch=${BRANCH};tag=${TAG};protocol=https \
            file://debian \
            file://defconfig \
           "

SECTION = "utils"
PRIORITY = "optional"


# When running do_build in qemu chroot context, installing
# mtd-utils-dev will overwrite files of package 'linux-libc-dev:armhf'
# For now overwrite them.
APT_EXTRA_OPTS = "-o Dpkg::Options::=--force-overwrite"

###                              ###
### debianize makefile functions ###
###                              ###
debianize_build[target] = "build"
debianize_build() {
	@echo "Running build target."
	cp ${PP}/defconfig ${PPS}/.config
	make
}


debianize_clean[target] = "clean"
debianize_clean() {
	@echo "Running clean target."
	dh_auto_clean
}


debianize_build-arch[target] = "build-arch"
debianize_build-arch() {
	@echo "Running build-arch target."
}


debianize_build-indep[target] = "build-indep"
debianize_build-indep() {
	@echo "Running build-indep target."
}


debianize_install[target] = "install"
debianize_install[tdeps] = "build"
debianize_install() {
	@echo "Running install target."
	dh_testdir
	dh_testroot
	dh_clean  -k

	install -m 755 -d debian/${BPN}/sbin
	install -m 755 swupdate debian/${BPN}/sbin
}



debianize_binary-arch[target] = "binary-arch"
debianize_binary-arch[tdeps] = "build install"
debianize_binary-arch() {
	@echo "Running binary-arch target."
	dh_testdir
	dh_testroot
	dh_installchangelogs
	dh_installdocs
	dh_installexamples
	dh_install
	dh_installman
	dh_link
	dh_strip
	dh_compress
	dh_fixperms
	dh_installdeb
	dh_shlibdeps --dpkg-shlibdeps-params=--ignore-missing-info
	dh_gencontrol
	dh_md5sums
	dh_builddeb
}


debianize_binary-indep[target] = "binary-indep"
debianize_binary-indep[tdeps] = "build install"
debianize_binary-indep() {
	@echo "Running binary-indep target."
}


debianize_binary[target] = "binary"
debianize_binary[tdeps] = "binary-arch binary-indep"
debianize_binary() {
	@echo "Running binary target."
}


#BBCLASSEXTEND = "native"




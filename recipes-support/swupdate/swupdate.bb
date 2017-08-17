DESCRIPTION = "Memory Technology Device Utilities. Utilities for manipulating memory technology devices, such as flash \
memory, Disk-On-Chip, or ROM.  Includes mkfs.jffs2, a tool to create \
JFFS2 (journaling flash file system) filesystems."
DESCRIPTION_DEV = " Memory Technology Device Utilities. Libraries and header files for developing."
LICENSE = "gpl2"

inherit dpkg debianize

DEPENDS += "mtd-utils"
DEB_DEPENDS += "liblua5.2-dev libconfig-dev libjson-c-dev libcurl4-gnutls-dev mtd-utils-dev libarchive-dev libubootenv python3-wget"

URL="git://github.com/sbabic/swupdate.git"
BRANCH="master"
#SRCREV = "${BRANCH}"
TAG="2017.04"

SRC_DIR = "git"
SRC_URI += "${URL};branch=${BRANCH};tag=${TAG};protocol=https \
            file://debian \
            file://defconfig \
            file://sw-description-${MACHINE} \
           "

SECTION = "utils"
PRIORITY = "optional"
SWUPDATE_HWREVISION = "${MACHINE} ${IMAGE_REVISION}"

# When running do_build in qemu chroot context, installing
# mtd-utils-dev will overwrite files of package 'linux-libc-dev:armhf'
# For now overwrite them.
APT_EXTRA_OPTS = "-o Dpkg::Options::=--force-overwrite"


# Checking for required variables
python() {
	hwrevision = d.getVar('IMAGE_REVISION', True) or ""

	if len(hwrevision) == 0:
		bb.fatal('IMAGE_REVISION not set, please do that in local.conf!')


	if len(hwrevision.split('.')) > 2:
		bb.fatal('IMAGE_REVISION is not correctly set. Syntax: <major>.<minor>')
}



do_pre_install_append() {
	install -m 0644 ${EXTRACTDIR}/sw-description-${MACHINE} ${DEPLOY_DIR_IMAGE}/sw-description.${DATETIME}-${MACHINE}
	ln -s ${DEPLOY_DIR_IMAGE}/sw-description.${DATETIME}-${MACHINE} sw-description
}


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

	install -m 755 -d debian/${BPN}/etc
	install -m 755 -d debian/${BPN}/sbin

	install -m 755 swupdate debian/${BPN}/sbin
	echo "${SWUPDATE_HWREVISION}" > debian/${BPN}/etc/hwrevision

	# Create a mapping for available root partitions in json format.
	# Required for selecting the correct partition update, in case of A-B updates.
	echo "{"                               >> debian/${BPN}/etc/update_partitions
	echo "\"${ROOTDEV_PRIM}\" : \"main\"," >> debian/${BPN}/etc/update_partitions
	echo "\"${ROOTDEV_SEC}\" : \"alt\""    >> debian/${BPN}/etc/update_partitions
	echo "}"                               >> debian/${BPN}/etc/update_partitions
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




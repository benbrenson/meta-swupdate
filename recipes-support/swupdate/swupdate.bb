DESCRIPTION = "Memory Technology Device Utilities. Utilities for manipulating memory technology devices, such as flash \
memory, Disk-On-Chip, or ROM.  Includes mkfs.jffs2, a tool to create \
JFFS2 (journaling flash file system) filesystems."
DESCRIPTION_DEV = " Memory Technology Device Utilities. Libraries and header files for developing."
LICENSE = "gpl2"

inherit dpkg debianize

DEPENDS += "mtd-utils-dev libubootenv"
DEPENDS_class-cross += "mtd-utils-dev-cross libubootenv-cross"

DEB_DEPENDS += "liblua5.2-dev libconfig-dev libjson-c-dev libcurl4-gnutls-dev libarchive-dev zlib1g-dev"
DEB_DEPENDS_class-cross += "liblua5.2-dev-cross libconfig-dev-cross libjson-c-dev-cross libcurl4-gnutls-dev-cross libarchive-dev-cross zlib1g-dev-cross"

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
SWUPDATE_HWREVISION = "${MACHINE} ${IMAGE_REVISION}"

# When running do_build in qemu chroot context, installing
# mtd-utils-dev will overwrite files of package 'linux-libc-dev:armhf'
# For now overwrite them.
APT_EXTRA_OPTS = "-o Dpkg::Options::=--force-overwrite"

MAKE = "make -j${PARALLEL_MAKE}"
MAKE_class-cross = "make CROSS_COMPILE=${TARGET_PREFIX}-"

# Checking for required variables
python() {
	hwrevision = d.getVar('IMAGE_REVISION', True) or ""

	if len(hwrevision) == 0:
		bb.fatal('IMAGE_REVISION not set, please do that in local.conf!')


	if len(hwrevision.split('.')) > 2:
		bb.fatal('IMAGE_REVISION is not correctly set. Syntax: <major>.<minor>')
}



do_pre_install_append() {
	cd ${DEPLOY_DIR_IMAGE}
	install -m 0644 ${EXTRACTDIR}/sw-description-${MACHINE} ${DEPLOY_DIR_IMAGE}/sw-description.${DATETIME}-${MACHINE}
	ln -sf sw-description.${DATETIME}-${MACHINE} sw-description
}


###                              ###
### debianize makefile functions ###
###                              ###
debianize_build[target] = "build"
debianize_build() {
	@echo "Running build target."
	cp ${PP}/defconfig ${PPS}/.config
	${MAKE}
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
	echo "{"                                                                    >> debian/${BPN}/etc/update_partitions
	echo "\"${base_devdir}/${ROOT_DEVICE_LINUX}${BOOTP_PRIM_NUM}\" : \"main_boot\"," >> debian/${BPN}/etc/update_partitions
	echo "\"${base_devdir}/${ROOT_DEVICE_LINUX}${BOOTP_PRIM_NUM}\" : \"alt_boot\","  >> debian/${BPN}/etc/update_partitions
	echo "\"${base_devdir}/${ROOT_DEVICE_LINUX}${ROOTP_PRIM_NUM}\" : \"main\"," >> debian/${BPN}/etc/update_partitions
	echo "\"${base_devdir}/${ROOT_DEVICE_LINUX}${ROOTP_SEC_NUM}\"  : \"alt\""   >> debian/${BPN}/etc/update_partitions
	echo "}"                                                                    >> debian/${BPN}/etc/update_partitions
}


BBCLASSEXTEND = "cross"


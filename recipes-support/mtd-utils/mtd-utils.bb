DESCRIPTION = "Memory Technology Device Utilities. Utilities for manipulating memory technology devices, such as flash \
memory, Disk-On-Chip, or ROM.  Includes mkfs.jffs2, a tool to create \
JFFS2 (journaling flash file system) filesystems."
DESCRIPTION_DEV = " Memory Technology Device Utilities. Libraries and header files for developing."
LICENSE = "gpl2"


inherit dpkg debianize

# Add build dependencies installed via apt-get
# to debian control file
DEB_DEPENDS += " zlib1g-dev liblzo2-dev uuid-dev "

BRANCH="master"
SRCREV="454a3d0b1ac413de3c32e4076ba74fdc70a8e973"
URL="git://git.infradead.org/mtd-utils.git"

SRC_DIR = "git"
SRC_URI += "${URL};branch=${BRANCH} \
           file://debian \
           "
SECTION = "utils"
PRIORITY = "extra"


MAKE = "make -j${PARALLEL_MAKE}"
PREFIX="/usr"


# Add description for dev package
do_generate_debcontrol_append() {
    sed -i -e 's/##DESCRIPTION_DEV##/${DESCRIPTION_DEV}/g'   ${CONTROL}
}


###                              ###
### debianize makefile functions ###
###                              ###
debianize_build[target] = "build"
debianize_build() {
	@echo "Running build target."
	autoreconf --force --install --symlink
	dh_auto_configure -- --prefix=${PREFIX}
	${MAKE}
}


debianize_install[target] = "install"
debianize_install[tdeps] = "build"
debianize_install() {
	@echo "Running install target."
	dh_testdir
	dh_testroot
	dh_clean  -k

	dh_auto_install
	dh_install
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


BBCLASSEXTEND = "native"
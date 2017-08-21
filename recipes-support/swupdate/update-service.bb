DESCRIPTION = "Daemon wrapping the swupdate software."
LICENSE = "gpl"

inherit dpkg debianize-python

RDEPENDS += "python3-paho-mqtt"
DEB_RDEPENDS += "python3-wget python3-configargparse"

URL = "git://git.pixel-group.de/siemens-ct/swupdate_python_daemon.git"
BRANCH = "master"
SRCREV = "${BRANCH}"

SRC_DIR = "git"
SRC_URI = "${URL};branch=${BRANCH};protocol=https \
           file://debian \
           file://update.conf \
           file://update.service \
          "

SECTION = "utils"
PRIORITY = "optional"

BROKER_IP ?= "192.168.8.1"
BROKER_PORT ?= "4242"
UPDATE_TOPIC ?= "cactus/dev/0a24871b-6eb0-47a3-a66b-a3ebca77e376-598b4e4460e570ea/device"

CFG="${EXTRACTDIR}/update.conf"
#Generate the update.conf file
do_generate_config(){
	sed -i -e 's|##broker-ip##|${BROKER_IP}|g'      ${CFG}
	sed -i -e 's|##broker-port##|${BROKER_PORT}|g'  ${CFG}
	sed -i -e 's|##update-topic##|${UPDATE_TOPIC}|g' ${CFG}
}
addtask do_generate_config after do_unpack before do_build

###                              ###
### debianize makefile functions ###
###                              ###
debianize_build[target] = "build"
debianize_build() {
	@echo "Running build target."
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

	install -m 755 -d debian/${BPN}/sbin
	install -m 755 -d debian/${BPN}/etc/update
	install -m 755 -d debian/${BPN}/lib/systemd/system

	install -m 755 command.py debian/${BPN}/sbin
	install -m 755 utils.py debian/${BPN}/sbin
	install -m 755 update_service debian/${BPN}/sbin

	install -m 644 ${PP}/update.conf debian/${BPN}/etc/update/update.conf


	install -m 644 ${PP}/update.service debian/${BPN}/lib/systemd/system/update.service
	dh_systemd_enable --name=update ${PP}/update.service
	dh_installinit --no-start --noscripts
	dh_systemd_start --no-start ${PP}/update.service
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

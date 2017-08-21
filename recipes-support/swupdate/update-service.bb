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


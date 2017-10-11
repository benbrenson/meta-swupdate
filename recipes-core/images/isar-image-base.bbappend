do_image_swupdate() {
    :
}
addtask do_image_swupdate after do_image before do_build


# _update specifies an OVERRIDE item, and is therefore
# not a part of the task name itself
do_image_swupdate_update() {
    bbwarn "Generating swu file for swupdate!"
    FILES="sw-description ${PN}.ext4"

    cd ${DEPLOY_DIR_IMAGE}
    for i in $FILES;do
        echo $i;done | cpio -ovL -H crc >  ${PN}.${DATETIME}.swu

    ln -sf ${PN}.${DATETIME}.swu ${PN}.swu
}

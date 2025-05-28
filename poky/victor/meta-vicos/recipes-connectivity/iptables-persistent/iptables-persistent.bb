DESCRIPTION = "iptables persistent service"
LICENSE = "Anki-Inc.-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/../victor/meta-qcom/files/anki-licenses/\
Anki-Inc.-Proprietary;md5=4b03b8ffef1b70b13d869dbce43e8f09"
                                                                                                    
inherit systemd qperf

FILESPATH =+ "${WORKSPACE}:"

#RULES_TAG = "${@base_conditional('USER_BUILD', '1', 'user', 'eng', d)}"                             
#RULES_TAG .= "${@base_conditional('DEV', '1', 'dev', '', d)}"
#RULES_TAG .= "${@base_conditional('OSKR', '1', 'oskr', '', d)}"

RULES_TAG = "${@bb.utils.contains('USER_BUILD','1','user','eng',d)}${@bb.utils.contains('DEV','1','dev','',d)}${@bb.utils.contains('OSKR','1','oskr','',d)}"


SRC_URI =  "file://iptables.service"
SRC_URI += "file://iptables-user.rules"
SRC_URI += "file://iptables-userdev.rules"
SRC_URI += "file://iptables-eng.rules"
SRC_URI += "file://iptables-engoskr.rules"
SRC_URI += "file://iptables-flush.sh"

SYSTEM_DIR = "${D}${sysconfdir}/systemd/system"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
  install -d ${D}/etc/iptables
  install -m 0644 ${UNPACKDIR}/iptables-flush.sh ${D}/etc/iptables/iptables-flush.sh
  install -m 0644 ${UNPACKDIR}/iptables-${RULES_TAG}.rules ${D}/etc/iptables/iptables.rules

  if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
    install -d  ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/iptables.service -D ${D}${systemd_unitdir}/system/iptables.service

    install -d ${SYSTEM_DIR}/
    install -d ${SYSTEM_DIR}/multi-user.target.wants/

    ln -sf ${systemd_unitdir}/system/iptables.service \
      ${SYSTEM_DIR}/multi-user.target.wants/iptables.service
  fi
}

RDEPENDS:${PN} += "iptables"

FILES:${PN} += "/etc/iptables/iptables.rules"
FILES:${PN} += "/etc/iptables/iptables-flush.sh"
FILES:${PN} += "/usr/lib/systemd/system"

#!/bin/sh
set -eu

root=$1
drop=$2
token=$3
directory=$4
destination=$5
temporary=$6
expected_sha256=$7
expected_root='/home/data/httpd/download.eclipse.org/modeling/emf/cdo/drops'

case "$drop" in
  R[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]) ;;
  *) echo "Invalid release drop ID" >&2; exit 2 ;;
esac

case "$token" in
  *[!0-9a-f]*|'') echo "Invalid temporary token" >&2; exit 2 ;;
esac

test "${#token}" -eq 32
case "$expected_sha256" in
  *[!0-9a-f]*|'') echo "Invalid expected marker SHA-256" >&2; exit 2 ;;
esac
test "${#expected_sha256}" -eq 64
test "$root" = "$expected_root"
test "$directory" = "$root/$drop"
test "$destination" = "$directory/maven-central.properties"
test "$temporary" = "$destination.tmp.$token"
test "$(realpath -e -- "$directory")" = "$directory"
test -f "$temporary" && test ! -L "$temporary"

trap 'rm -f -- "$temporary"' 0
trap 'exit 1' HUP INT TERM

temporary_sha256=$(sha256sum "$temporary" | awk '{print $1}')
test "$temporary_sha256" = "$expected_sha256"
chmod 0644 "$temporary"

if [ -e "$destination" ] || [ -L "$destination" ]; then
  if [ -f "$destination" ] && [ ! -L "$destination" ] && [ "$(sha256sum "$destination" | awk '{print $1}')" = "$expected_sha256" ]; then
    chmod 0644 "$destination"
    test "$(sha256sum "$destination" | awk '{print $1}')" = "$expected_sha256"
    echo "ALREADY_PRESENT"
    exit 0
  fi

  echo "Existing Maven Central marker conflicts with verified content" >&2
  exit 17
fi

if ! ln -T -- "$temporary" "$destination"; then
  if [ -f "$destination" ] && [ ! -L "$destination" ] && [ "$(sha256sum "$destination" | awk '{print $1}')" = "$expected_sha256" ]; then
    chmod 0644 "$destination"
    test "$(sha256sum "$destination" | awk '{print $1}')" = "$expected_sha256"
    echo "ALREADY_PRESENT"
    exit 0
  fi

  echo "Could not atomically publish Maven Central marker" >&2
  exit 18
fi

published_sha256=$(sha256sum "$destination" | awk '{print $1}')
test "$published_sha256" = "$expected_sha256" || { echo "Published marker content verification failed" >&2; exit 19; }
test "$(stat -c '%a' "$destination")" = '644' || { echo "Published marker is not publicly readable" >&2; exit 20; }
echo "PUBLISHED"

#!/bin/sh
set -eu

script_directory=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
test_root=$(mktemp -d "${TMPDIR:-/tmp}/cdo-marker-test.XXXXXX")
trap 'rm -rf -- "$test_root"' 0
trap 'exit 1' HUP INT TERM

drop_root="$test_root/drops"
drop_id='R20260901-1311'
drop_directory="$drop_root/$drop_id"
destination="$drop_directory/maven-central.properties"
marker="$test_root/maven-central.properties"
mkdir -p -- "$drop_directory"
cat > "$marker" <<'MARKER'
artifact.one=1.0.0
artifact.two=1.0.0
artifact.three=1.0.0
artifact.four=1.0.0
artifact.five=1.0.0
artifact.six=1.0.0
artifact.seven=1.0.0
artifact.eight=1.0.0
artifact.nine=1.0.0
artifact.ten=1.0.0
artifact.eleven=1.0.0
artifact.twelve=1.0.0
artifact.bom=1.0.0
MARKER
expected_sha256=$(sha256sum "$marker" | awk '{print $1}')
test "${#expected_sha256}" -eq 64

remote_script="$test_root/publish-marker-remote.sh"
sed "s|^expected_root='/home/data/httpd/download.eclipse.org/modeling/emf/cdo/drops'$|expected_root='$drop_root'|" \
  "$script_directory/publish-marker-remote.sh" > "$remote_script"
chmod 0700 "$remote_script"
grep -F "expected_root='$drop_root'" "$remote_script" >/dev/null

publish()
{
  token=$1
  temporary="$destination.tmp.$token"
  cp -- "$marker" "$temporary"
  sh "$remote_script" "$drop_root" "$drop_id" "$token" "$drop_directory" "$destination" "$temporary" "$expected_sha256"
}

first_token='11111111111141118111111111111111'
first_result=$(publish "$first_token")
test "$first_result" = 'PUBLISHED'
test "$(sha256sum "$destination" | awk '{print $1}')" = "$expected_sha256"
test "$(stat -c '%a' "$destination")" = '644'
test ! -e "$destination.tmp.$first_token"

second_token='22222222222242228222222222222222'
chmod 0600 "$destination"
second_result=$(publish "$second_token")
test "$second_result" = 'ALREADY_PRESENT'
test "$(sha256sum "$destination" | awk '{print $1}')" = "$expected_sha256"
test "$(stat -c '%a' "$destination")" = '644'
test ! -e "$destination.tmp.$second_token"

concurrent_directory="$drop_root/R20260902-1311"
concurrent_destination="$concurrent_directory/maven-central.properties"
mkdir -p -- "$concurrent_directory"
token_a='aaaaaaaaaaaa4aaa8aaaaaaaaaaaaaaa'
token_b='bbbbbbbbbbbb4bbb8bbbbbbbbbbbbbbb'
temporary_a="$concurrent_destination.tmp.$token_a"
temporary_b="$concurrent_destination.tmp.$token_b"
cp -- "$marker" "$temporary_a"
cp -- "$marker" "$temporary_b"
sh "$remote_script" "$drop_root" 'R20260902-1311' "$token_a" "$concurrent_directory" "$concurrent_destination" "$temporary_a" "$expected_sha256" > "$test_root/concurrent-a.out" 2> "$test_root/concurrent-a.err" &
pid_a=$!
sh "$remote_script" "$drop_root" 'R20260902-1311' "$token_b" "$concurrent_directory" "$concurrent_destination" "$temporary_b" "$expected_sha256" > "$test_root/concurrent-b.out" 2> "$test_root/concurrent-b.err" &
pid_b=$!
wait "$pid_a"
wait "$pid_b"
case "$(cat "$test_root/concurrent-a.out")" in PUBLISHED|ALREADY_PRESENT) ;; *) exit 1 ;; esac
case "$(cat "$test_root/concurrent-b.out")" in PUBLISHED|ALREADY_PRESENT) ;; *) exit 1 ;; esac
test "$(sha256sum "$concurrent_destination" | awk '{print $1}')" = "$expected_sha256"
test ! -e "$temporary_a"
test ! -e "$temporary_b"

printf 'conflicting content\n' > "$destination"
conflict_sha256=$(sha256sum "$destination" | awk '{print $1}')
conflict_token='33333333333343338333333333333333'
temporary="$destination.tmp.$conflict_token"
cp -- "$marker" "$temporary"
if sh "$remote_script" "$drop_root" "$drop_id" "$conflict_token" "$drop_directory" "$destination" "$temporary" "$expected_sha256" > "$test_root/conflict.out" 2> "$test_root/conflict.err"; then
  echo "Conflicting marker content was accepted" >&2
  exit 1
else
  test "$?" -eq 17
fi
test "$(sha256sum "$destination" | awk '{print $1}')" = "$conflict_sha256"
test ! -e "$temporary"

invalid_id='I20260901-1311'
invalid_directory="$drop_root/$invalid_id"
mkdir -p -- "$invalid_directory"
invalid_destination="$invalid_directory/maven-central.properties"
invalid_token='44444444444444448444444444444444'
invalid_temporary="$invalid_destination.tmp.$invalid_token"
cp -- "$marker" "$invalid_temporary"
if sh "$remote_script" "$drop_root" "$invalid_id" "$invalid_token" "$invalid_directory" "$invalid_destination" "$invalid_temporary" "$expected_sha256" > "$test_root/invalid.out" 2> "$test_root/invalid.err"; then
  echo "Non-release drop ID was accepted" >&2
  exit 1
else
  test "$?" -eq 2
fi
test ! -e "$invalid_destination"
rm -f -- "$invalid_temporary"

failure_directory="$drop_root/R20260903-1311"
mkdir -p -- "$failure_directory"
failure_destination="$failure_directory/maven-central.properties"
failure_token='55555555555545558555555555555555'
failure_temporary="$failure_destination.tmp.$failure_token"
cp -- "$marker" "$failure_temporary"
if sh "$remote_script" "$drop_root" 'R20260903-1311' "$failure_token" "$failure_directory" "$failure_destination" "$failure_temporary" '0000000000000000000000000000000000000000000000000000000000000000' > "$test_root/failure.out" 2> "$test_root/failure.err"; then
  echo "Mismatched upload checksum was accepted" >&2
  exit 1
fi
test ! -e "$failure_destination"
test ! -e "$failure_temporary"

echo "Remote marker script passed initial, idempotent, concurrent, conflicting, invalid-drop, checksum, and cleanup cases."

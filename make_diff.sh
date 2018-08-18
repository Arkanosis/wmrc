#! /bin/sh

OUT="$HOME/Web/diff.html"

cat >| "$OUT" <<EOF
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<style type="text/css">
			.diff-lineno {
				font-weight: bold
			}
			td.diff-marker {
				text-align: right;
				font-weight: bold;
				font-size: 1.25em;
				line-height: 1.2;
			}
			.diff-addedline, .diff-deletedline, .diff-context {
				font-size: 150%; /* TODO make this configurable by the user */
				line-height: 1.6;
				vertical-align: top;
				white-space: -moz-pre-wrap;
				white-space: pre-wrap;
				border-style: solid;
				border-width: 1px 1px 1px 4px;
				border-radius: 0.33em;
			}
			.diff-addedline {
				/*border-color: #a3d3ff;*/ /* TODO this and below: propose themes: GitHub, MediaWiki and others */
				border-color: #bef5cb;
			}
			.diff-deletedline {
				/*border-color: #ffe49c;*/
				border-color: #fdaeb7;
			}
			.diff-context {
				background: #f8f9fa;
				border-color: #eaecf0;
				color: #222;
			}
			.diffchange {
				font-weight: bold;
				text-decoration: none;
			}
			.diff-addedline .diffchange, .diff-deletedline .diffchange {
				border-radius: 0.33em;
				padding: 0.25em 0;
			}
			.diff-addedline .diffchange {
				/*background: #d8ecff;*/
				background: #bef5cb;
			}
			.diff-deletedline .diffchange {
				/*background: #feeec8;*/
				background: #fdaeb7;
			}
		</style>
	</head>
	<body>
		<table>
EOF

curl 'https://en.wikipedia.org/w/api.php?action=compare&torelative=prev&fromrev=844603014&format=json' |
    jq -r '.compare["*"]' >> "$OUT"

cat >> "$OUT" <<EOF
		</table>
	</body>
</html>
EOF

firefox "$OUT"

(function ($) {
    $(document).ready(function () {

        var exportBaseUrl = $('input[name="citation-export-base"]').val();
        var exportButton = $('button.citation-export-button');

        var selectedCitationStyleId = -1;
        var selectedFormat = $('input[name="citation-format-radio"]:checked').val();

        $('.citation-style').find('a').click(function (e) {
            e.preventDefault();
            selectedCitationStyleId = $(this).attr('href');
            $('#citationFormatModal').modal('show');
        });

        $('.citation-format-radio').find('label').click(function (e) {
            selectedFormat = $(this).find('input').val();
        });

        $('button.citation-export-button').click(function (e) {
            performExport(exportBaseUrl, selectedCitationStyleId, selectedFormat);
        });


        function performExport(baseUrl, citationStyle, format) {
            var url = baseUrl + '&citationStyle=' + citationStyle + '&format=' + format;
            window.location.assign(url);

        }


    });
})(jQuery);
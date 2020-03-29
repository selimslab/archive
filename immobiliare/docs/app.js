/* global instantsearch algoliasearch */

const search = instantsearch({
    indexName: 'immobiliare',
    searchClient: algoliasearch('CCS0JY0UM1', '9f01cfca9e1b88b7f689a744c96707a6'),
});


search.addWidget(
    instantsearch.widgets.searchBox({
        container: '#searchbox',
    })
);


search.addWidget(
    instantsearch.widgets.hits({
        container: '#hits',
        templates: {
            item: `


          <img class=card-img src="{{images.0}}" align="left" alt="{{name}}" />
    
          

          <p>{{#helpers.highlight}}{ "attribute": "il" }{{/helpers.highlight}}
              {{#helpers.highlight}}{ "attribute": "ilce" }{{/helpers.highlight}}

        {{#helpers.highlight}}{ "attribute": "semt" }{{/helpers.highlight}}

          </p>
          <p>{{title}}</p>

          <p>{{İlan Tarihi}}</p>
          <p>{{price}} TL</p>
          <p>{{office.name}}</p>

      `,
        },
    })
);

search.addWidget(
    instantsearch.widgets.pagination({
        container: '#pagination',
    })
);

search.start();



